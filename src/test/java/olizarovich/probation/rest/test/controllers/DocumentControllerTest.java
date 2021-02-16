package olizarovich.probation.rest.test.controllers;

import com.google.gson.Gson;
import olizarovich.probation.rest.config.ApplicationConfig;
import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.models.Role;
import olizarovich.probation.rest.repositories.DocumentRepository;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.repositories.RoleRepository;
import olizarovich.probation.rest.specifications.SpecificationsBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApplicationConfig.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude= SecurityAutoConfiguration.class)
@AutoConfigureTestDatabase
public class DocumentControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private DocumentRepository repository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private RoleRepository roleRepository;

    public void resetDb() {
        repository.deleteAll();
        roleRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    public void givenDocument_whenGetDocuments_thenStatus200() throws Exception {
        resetDb();

        LocalDate date = LocalDate.of(2000, 10, 10);
        LocalDate datePlusMonth =  LocalDate.of(2000, 11, 11);

        Person ex = createTestPerson("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        Person cust = createTestPerson("alex", "alex", "alex", "alex", LocalDate.of(2000, 11, 11));

        createTestDocument("Title1", "status1", date, datePlusMonth, cust, ex);
        createTestDocument("Title2", "status2", date, datePlusMonth, ex, cust);
        createTestDocument("Title3", "status3", date, datePlusMonth, ex, cust);

        mvc.perform(get("/documents").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                .andExpect(jsonPath("$[0].title", is("Title1")))
                .andExpect(jsonPath("$[1].status", is("status2")));
    }

    @Test
    public void givenDocument_whenFindDocument_thenStatus200() throws Exception {
        resetDb();

        LocalDate date = LocalDate.of(2000, 10, 10);
        LocalDate datePlusMonth =  LocalDate.of(2000, 11, 11);

        Person ex = createTestPerson("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        Person cust = createTestPerson("alex", "alex", "alex", "alex", LocalDate.of(2000, 11, 11));

        createTestDocument("Title1", "status1", date, datePlusMonth, cust, ex);
        Document doc = createTestDocument("Title2", "status2", date, datePlusMonth, ex, cust);
        createTestDocument("Title3", "status3", date, datePlusMonth, ex, cust);

        mvc.perform(get("/documents/" + doc.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Title2")));
    }

    @Test
    public void givenDocument_whenDeleteStatus_thenStatus200() throws Exception {
        resetDb();
        LocalDate date = LocalDate.of(2000, 10, 10);
        LocalDate datePlusMonth =  LocalDate.of(2000, 11, 11);

        Person ex = createTestPerson("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        Person cust = createTestPerson("alex", "alex", "alex", "alex", LocalDate.of(2000, 11, 11));

        createTestDocument("Title1", "status1", date, datePlusMonth, cust, ex);
        Document doc = createTestDocument("Title2", "status2", date, datePlusMonth, ex, cust);
        createTestDocument("Title3", "status3", date, datePlusMonth, ex, cust);

        mvc.perform(delete("/documents/"+ doc.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        SpecificationsBuilder<Document> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("isDeleted", ":", true);

        List<Document> found = repository.findAll(specificationsBuilder.build());
        assertThat(found.size()).isEqualTo(1);
    }

    private Document createTestDocument(String title, String status,
                                      LocalDate creationDate,  LocalDate executionPeriod,
                                      Person customer, Person executor) {
        Document doc = new Document();
        doc.setTitle(title);
        doc.setStatus(status);
        doc.setCreationDate(creationDate);
        doc.setExecutionPeriod(executionPeriod);
        doc.setCustomer(customer);
        doc.setExecutor(executor);

        doc = repository.saveAndFlush(doc);

        return doc;
    }

    private Person createTestPerson(String username, String password,
                                    String firstName, String lastName, LocalDate localDate) {
        Person emp = new Person(username, password, firstName, lastName, localDate);
        emp = personRepository.saveAndFlush(emp);

        Role role = new Role();
        role.setPersonId(emp.getId());
        role.setRole("USER");
        roleRepository.saveAndFlush(role);

        return emp;
    }
}
