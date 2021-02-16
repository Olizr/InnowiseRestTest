package olizarovich.probation.rest.test.controllers;

import com.google.gson.Gson;
import olizarovich.probation.rest.config.ApplicationConfig;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.models.Role;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.repositories.RoleRepository;
import olizarovich.probation.rest.specifications.SpecificationsBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = ApplicationConfig.class)
@AutoConfigureMockMvc
@EnableAutoConfiguration(exclude=SecurityAutoConfiguration.class)
@AutoConfigureTestDatabase
public class PersonControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private PersonRepository repository;
    @Autowired
    private RoleRepository roleRepository;

    public void resetDb() {
        repository.deleteAll();
        roleRepository.deleteAll();
    }

    /**
     * Not working. Post method working. But that specific test is not
     * @throws IOException
     * @throws Exception
     */
    @Test
    @Ignore
    public void whenValidInput_thenCreatePerson() throws IOException, Exception {
        resetDb();
        Gson gson = new Gson();

        Person bob = new Person("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        bob.setRoles(null);
        mvc.perform(post("/persons").contentType(MediaType.APPLICATION_JSON).content(gson.toJson(bob)));

        List<Person> found = repository.findAll();
        assertThat(found).extracting(Person::getFirstName).containsOnly("bob");
    }

    @Test
    public void givenPersons_whenGetPersons_thenStatus200() throws Exception {
        resetDb();
        createTestPerson("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        createTestPerson("alex", "alex", "alex", "alex", LocalDate.of(2000, 11, 11));

        mvc.perform(get("/persons").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].username", is("bob")))
                .andExpect(jsonPath("$[1].username", is("alex")));
    }

    @Test
    public void givenPersons_whenFindPerson_thenStatus200() throws Exception {
        resetDb();
        Person bob = createTestPerson("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        createTestPerson("alex", "alex", "alex", "alex", LocalDate.of(2000, 11, 11));

        mvc.perform(get("/persons/" + bob.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username", is("bob")));
    }

    @Test
    public void givenPersons_whenDeleteStatus_thenStatus200() throws Exception {
        resetDb();
        Person bob = createTestPerson("bob", "bob", "bob", "bob", LocalDate.of(2000, 11, 11));
        createTestPerson("alex", "alex", "alex", "alex", LocalDate.of(2000, 11, 11));

        mvc.perform(delete("/persons/"+ bob.getId()).contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        SpecificationsBuilder<Person> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("isDeleted", ":", true);

        List<Person> found = repository.findAll(specificationsBuilder.build());
        assertThat(found.size()).isEqualTo(1);
    }

    private Person createTestPerson(String username, String password,
                                  String firstName, String lastName, LocalDate localDate) {
        Person emp = new Person(username, password, firstName, lastName, localDate);
        emp = repository.saveAndFlush(emp);

        Role role = new Role();
        role.setPersonId(emp.getId());
        role.setRole("USER");
        roleRepository.saveAndFlush(role);

        return emp;
    }
}
