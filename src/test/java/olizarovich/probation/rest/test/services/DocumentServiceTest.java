package olizarovich.probation.rest.test.services;

import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.services.DocumentService;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DocumentServiceTest {
    @Autowired
    private DocumentService service;

    @Autowired
    private TestEntityManager entityManager;

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
     * Creates and inserts document entities to database
     *
     * @param documents List of documents to insert
     * @return List of document with Ids
     */
    private List<Document> loadDocumentData(List<Document> documents) {
        for (Document i : documents) {
            entityManager.persist(i);
            entityManager.flush();
        }

        return documents;
    }

    /**
     * Creates and loads persons and document into database
     * @return List of Documents with database ids
     */
    private List<Document> loadData() {
        List<Person> persons = DataInit.createPersonsData(3);
        loadPersonsDate(persons);

        List<Document> documents = DataInit.createDocumentsData(persons);
        loadDocumentData(documents);

        return documents;
    }

    @Test
    public void findAllServiceTest() {
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();
        service.findAll().forEach(found::add);

        assertEquals(documents.size(), found.size());
    }

    @Test
    public void findAllWithSortServiceTest() {
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();

        service.setSort(DocumentService.DocumentSort.TITLE).findAll().forEach(found::add);
        documents = documents.stream().sorted(Comparator.comparing(Document::getTitle)).collect(Collectors.toList());

        assertEquals(documents.size(), found.size());
    }

    @Test
    public void findAllWithSortByForeignKeyServiceTest() {
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();

        service.setSort(DocumentService.DocumentSort.CUSTOMERFIRSTNAME).findAll().forEach(found::add);
        documents = documents.stream().sorted(Comparator.comparing(d -> d.getCustomer().getFirstName())).collect(Collectors.toList());

        assertEquals(documents.size(), found.size());
    }

    /**
     * Testing service pagination
     */
    @Test
    public void getPageServiceTest() {
        int pageSize = 2;
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();

        service.toPage(0, pageSize).forEach(found::add);

        assertEquals(pageSize, found.size());
    }

    @Test
    public void findAllWithFilterServiceTest() {
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();
        String titleToFind = documents.get(1).getTitle();

        service.filterByTitle(titleToFind);
        service.findAll().forEach(found::add);

        documents = documents.stream().filter(p -> p.getTitle().contains(titleToFind)).collect(Collectors.toList());
        assertEquals(documents.size(), found.size());
    }

    @Test
    public void findAllWithFilterForeignKeyServiceTest() {
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();
        String nameToFind = documents.get(1).getCustomer().getFirstName();

        service.filterByTitle(nameToFind);
        service.findAll().forEach(found::add);

        documents = documents.stream().filter(p -> p.getCustomer().getFirstName().contains(nameToFind)).collect(Collectors.toList());
        assertEquals(documents.size(), found.size());
    }

    @Test
    public void countWithFilterServiceTest() {
        List<Document> documents = loadData();
        String titleToFind = documents.get(1).getTitle();

        service.filterByTitle(titleToFind);

        documents = documents.stream().filter(p -> p.getTitle().contains(titleToFind)).collect(Collectors.toList());

        assertEquals(documents.size(), service.count());
    }

    @Test
    public void getByIdServiceTest() {
        List<Document> documents = loadData();
        Document toFind = documents.get(2);
        Document found = service.findById(toFind.getId()).get();

        assertEquals(toFind, found);
    }

    @Test
    public void saveEntityServiceTest() {
        List<Person> persons = DataInit.createPersonsData(3);
        loadPersonsDate(persons);
        List<Document> documents = DataInit.createDocumentsData(persons);

        service.saveAll(documents);

        List<Document> found = new ArrayList<>();
        service.findAll().forEach(found::add);

        assertEquals(documents.size(), found.size());
    }

    @Test
    public void existsByIdServiceTest() {
        List<Document> documents = loadData();
        Document toFind = documents.get(2);

        assertTrue(service.existsById(toFind.getId()));

        toFind = documents.get(documents.size() - 1);
        assertFalse(service.existsById(toFind.getId() + 10));
    }

    @Test
    public void deleteByIdServiceTest() {
        List<Document> documents = loadData();
        List<Document> found = new ArrayList<>();
        Document toDelete = documents.get(2);

        service.deleteById(toDelete.getId());

        service.searchOnlyInDeleted().findAll().forEach(found::add);

        assertEquals(1, found.size());
    }

    /**
     * Testing deletion using documents list.
     * Searching entity that marked with idDeleted == true
     */
    @Test
    public void deleteAllAndSearchInDeletedServiceTest() {
        List<Document> documents = loadData();
        List<Document> toDelete = new ArrayList<>();

        for (int i = 0; i < documents.size() / 2; i++) {
            toDelete.add(documents.get(i));
        }

        service.deleteAll(toDelete);

        service.searchOnlyInDeleted();
        long total = service.count();
        assertEquals(toDelete.size(), total);
    }
    
    @AfterEach
    public void clearService(){
        service.clearFilter();
    }
}
