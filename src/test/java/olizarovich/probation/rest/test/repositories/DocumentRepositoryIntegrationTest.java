package olizarovich.probation.rest.test.repositories;

import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.repositories.DocumentRepository;
import olizarovich.probation.rest.specifications.SpecificationsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Class for testing DocumentRepository using h2 in memory database
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DocumentRepository documentRepository;

    /**
     * Creates 3 persons entities
     *
     * @return List of persons
     */
    private List<Person> createPersonsData() {
        List<Person> persons = new ArrayList<>();
        LocalDate localDate = LocalDate.of(2000, 1, 1);
        persons.add(new Person("alex", "xela", localDate));
        persons.add(new Person("John", "njoJ", localDate));
        persons.add(new Person("Aasd", "dsaA", localDate));

        return persons;
    }

    /**
     * Load list of persons to database
     *
     * @param persons List of persons to insert
     */
    private void loadPersonsDate(List<Person> persons) {
        for (Person i : persons) {
            entityManager.persist(i);
            entityManager.flush();
        }
    }

    /**
     * Creates 9 documents entities. Users list of person for fields.
     *
     * @param persons List of persons for foreign Key
     * @return List of persons
     */
    private List<Document> createDocumentsData(List<Person> persons) {
        ArrayList<Document> documents = new ArrayList<>();
        String[] documentsStatus = new String[]{"Ready", "In progress", "Unknown"};

        for (Person i : persons) {
            int statusNumber = 0;
            for (Person j : persons) {
                Document document = new Document();
                document.setTitle(i.getFirstName() + "Document");
                document.setStatus(documentsStatus[statusNumber]);
                document.setCreationDate(LocalDate.of(2000, 1, 1).plusMonths(statusNumber));
                document.setExecutionPeriod(LocalDate.of(2000, 2, 1).plusMonths(statusNumber));
                document.setCustomer(i);
                document.setExecutor(j);

                documents.add(document);
                statusNumber++;
            }
        }

        return documents;
    }

    /**
     * Creates and inserts document entities to database
     *
     * @return List of document with Ids
     */
    private List<Document> initTestData() {
        List<Person> persons = createPersonsData();
        loadPersonsDate(persons);

        List<Document> documents = createDocumentsData(persons);

        for (Document i : documents) {
            entityManager.persist(i);
            entityManager.flush();
        }

        return documents;
    }

    /**
     * Testing findAll repository methods
     */
    @Test
    public void testReadDataFromDatabase() {
        List<Document> documents = initTestData();

        List<Document> found = documentRepository.findAll();

        assertEquals(documents.size(), found.size());
    }

    /**
     * Testing findAll repository methods with pagination
     */
    @Test
    public void testPageFilter() {
        List<Document> documents = initTestData();

        Pageable pageable = PageRequest.of(0, 2);

        List<Document> found = documentRepository.findAll(pageable).toList();

        assertEquals(2, found.size());
    }

    /**
     * Testing findAll repository methods with equality filter
     */
    @Test
    public void testEqualFilter() {
        List<Document> documents = initTestData();

        SpecificationsBuilder<Document> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("creationDate", ":", LocalDate.of(2000, 1, 1));

        List<Document> found = documentRepository.findAll(specificationsBuilder.build());

        assertEquals(3, found.size());
    }

    /**
     * Testing findAll repository methods with equality filter on foreign key
     */
    @Test
    public void testEqualFilterForeignKey() {
        List<Document> documents = initTestData();

        SpecificationsBuilder<Document> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("customer.id", ":", documents.get(0).getCustomer().getId());

        List<Document> found = documentRepository.findAll(specificationsBuilder.build());

        assertEquals(3, found.size());
    }

    /**
     * Testing findAll repository methods with equality filter and pagination
     */
    @Test
    public void testPageSort() {
        List<Document> documents = initTestData();

        Pageable pageable = PageRequest.of(0, 9, Sort.by("creationDate"));

        List<Document> found = documentRepository.findAll(pageable).toList();

        documents.sort(Comparator.comparing(Document::getCreationDate));
        assertEquals(9, found.size());
    }

    /**
     * Testing save method. Should return all entity that was saved
     */
    @Test
    public void testSaveDataToDatabase() {
        List<Person> persons = createPersonsData();
        loadPersonsDate(persons);

        List<Document> documents = createDocumentsData(persons);
        for (Document i : documents) {
            documentRepository.save(i);
        }

        List<Document> found = documentRepository.findAll();

        assertEquals(documents.size(), found.size());
    }

    /**
     * Testing update method. Changes title of entity.
     * And checking if changes applied.
     */
    @Test
    public void testUpdateEntity() {
        String newTitle = "AAAA";
        List<Document> documents = initTestData();

        int documentId = documents.get(0).getId();
        Document document = documentRepository.findById(documentId).get();


        document.setTitle(newTitle);
        documentRepository.save(document);

        document = documentRepository.getOne(documentId);
        assertEquals(newTitle, document.getTitle());
    }

    /**
     * Testing softDelete to set isDeleted field instead of delete row
     * Soft delete by Id
     */
    @Test
    public void testSoftDeleteById() {
        List<Document> documents = initTestData();

        documentRepository.softDeleteById(documents.get(0).getId());

        assertEquals(1, documentRepository.recycleBin().size());
    }

    /**
     * Testing softDelete to set isDeleted field instead of delete row
     * Soft delete by entity
     */
    @Test
    public void testSoftDelete() {
        List<Document> documents = initTestData();

        documentRepository.softDelete(documents.get(0));

        assertEquals(1, documentRepository.recycleBin().size());
    }

    /**
     * Testing softDelete to set isDeleted field instead of delete row
     * Soft delete on list of documentsIds
     */
    @Test
    public void testSoftDeleteAll() {
        List<Document> documents = initTestData();

        List<Document> documentsToDelete = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            documentsToDelete.add(documents.get(i));
        }

        documentRepository.softDeleteAll(documentsToDelete.stream().mapToInt(Document::getId).boxed()
                .collect(Collectors.toList()));

        assertEquals(3, documentRepository.recycleBin().size());
    }
}
