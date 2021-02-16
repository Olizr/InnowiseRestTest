package olizarovich.probation.rest.test.services;

import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.models.Role;
import olizarovich.probation.rest.repositories.DocumentRepository;
import olizarovich.probation.rest.services.DocumentService;
import olizarovich.probation.rest.services.implementation.DocumentServiceImplementation;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RunWith(MockitoJUnitRunner.class)
public class DocumentServiceTest {
    @Mock
    private DocumentRepository mockDocumentRepository;

    private DocumentService service;
    private Document documentToInsert;
    private Document documentToReturn;
    private Document invalidDocument;
    private List<Document> documentTestData;
    private List<Person> personsTestData;

    private Role roleToInsert;

    @Before
    public void setUp() {
        service = new DocumentServiceImplementation(mockDocumentRepository);

        personsTestData = DataInit.createPersonsData(3);
        documentTestData = DataInit.createDocumentsData(personsTestData);

        documentToInsert = documentTestData.get(1);
        documentToInsert = documentTestData.get(2);

        invalidDocument = new Document();

        roleToInsert = new Role();
        roleToInsert.setPersonId(1);
        roleToInsert.setRole("USER");

        Page<Document> page = new PageImpl<Document>(documentTestData.stream().limit(2).collect(Collectors.toList()));

        Mockito.when(mockDocumentRepository.save(any()))
                .thenReturn(documentToInsert);
        Mockito.when(mockDocumentRepository.findAll( any(Specification.class)))
                .thenReturn(documentTestData);
        Mockito.when(mockDocumentRepository.findAll( any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        Mockito.when(mockDocumentRepository.findById(anyInt()))
                .thenReturn(Optional.of(documentToInsert));
    }

    /**
     * Create data and loads it into database
     */
    public List<Person> initData() {
        List<Person> persons = DataInit.createPersonsData(5);
        return persons;
    }

    @Test
    public void findAllServiceTest() {
        List<Document> found = new ArrayList<>();

        service.findAll().forEach(found::add);

        assertEquals(documentTestData.size(), found.size());
    }

    /**
     * Testing service pagination
     */
    @Test
    public void getPageServiceTest() {
        int pageSize = 2;
        List<Document> found = new ArrayList<>();

        service.toPage(0, pageSize).forEach(found::add);

        assertEquals(pageSize, found.size());
    }

    @Test
    public void getByIdServiceTest() {
        Document toFind = documentToInsert;
        Document found = service.findById(toFind.getId()).get();

        assertEquals(toFind, found);
    }

    @Test
    public void saveEntityServiceTest() {
        for (Document i : documentTestData) {
            service.save(i);
        }

        List<Document> found = new ArrayList<>();
        service.findAll().forEach(found::add);

        assertEquals(documentTestData.size(), found.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveInvalidEntityServiceTest() {
        Document found = service.save(invalidDocument);

        assertEquals(documentToInsert, found);
    }

    @AfterEach
    public void clearService(){
        service.clearFilter();
    }
}
