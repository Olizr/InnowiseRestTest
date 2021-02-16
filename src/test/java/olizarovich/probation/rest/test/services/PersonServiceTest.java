package olizarovich.probation.rest.test.services;

import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.models.Role;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.repositories.RoleRepository;
import olizarovich.probation.rest.services.PersonService;
import olizarovich.probation.rest.services.implementation.PersonServiceImplementation;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;

/**
 * Class for testing service layer
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonServiceTest {
    @Mock
    private PersonRepository mockPersonRepository;
    @Mock
    private RoleRepository mockRoleRepository;
    @Mock
    private BCryptPasswordEncoder mockBCryptPasswordEncoder;

    private PersonService service;
    private Person personToInsert;
    private Person personToReturn;
    private Person invalidPerson;
    private List<Person> personsTestData;

    private Role roleToInsert;

    @Before
    public void setUp() {
        service = new PersonServiceImplementation(mockBCryptPasswordEncoder,
                mockRoleRepository, mockPersonRepository);

        personToInsert = new Person();
        personToInsert.setId(1);
        personToInsert.setUsername("Gustavo");
        personToInsert.setPassword("Gustavo");
        personToInsert.setFirstName("Gustavo");
        personToInsert.setLastName("Ponce");
        personToInsert.setBirthDate(LocalDate.of(2000, 11, 11));

        personToReturn = new Person();
        personToReturn.setId(2);
        personToReturn.setUsername("Gust");
        personToReturn.setPassword("Gust");
        personToReturn.setFirstName("Guso");
        personToReturn.setLastName("Pon");
        personToReturn.setBirthDate(LocalDate.of(2000, 11, 11));

        invalidPerson = new Person();

        roleToInsert = new Role();
        roleToInsert.setPersonId(1);
        roleToInsert.setRole("USER");

        personsTestData = DataInit.createPersonsData(5);

        Page<Person> page = new PageImpl<Person>(personsTestData.stream().limit(2).collect(Collectors.toList()));

        Mockito.when(mockPersonRepository.save(any()))
                .thenReturn(personToInsert);
        Mockito.when(mockPersonRepository.findAll(any(Specification.class)))
                .thenReturn(personsTestData);
        Mockito.when(mockPersonRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(page);
        Mockito.when(mockPersonRepository.findById(anyInt()))
                .thenReturn(Optional.of(personToInsert));
        Mockito.when(mockPersonRepository.findByUsername(anyString()))
                .thenReturn(null);

        Mockito.when(mockRoleRepository.save(any()))
                .thenReturn(roleToInsert);

        Mockito.when(mockBCryptPasswordEncoder.encode(anyString()))
                .thenReturn("password");
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
        List<Person> found = new ArrayList<>();

        service.findAll().forEach(found::add);

        assertEquals(personsTestData.size(), found.size());
    }

    /**
     * Testing service pagination
     */
    @Test
    public void getPageServiceTest() {
        int pageSize = 2;
        List<Person> found = new ArrayList<>();

        service.toPage(0, pageSize).forEach(found::add);

        assertEquals(pageSize, found.size());
    }

    @Test
    public void getByIdServiceTest() {
        Person toFind = personToInsert;
        Person found = service.findById(toFind.getId()).get();

        assertEquals(toFind, found);
    }

    @Test
    public void saveEntityServiceTest() {
        for (Person i : personsTestData) {
            service.save(i);
        }

        List<Person> found = new ArrayList<>();
        service.findAll().forEach(found::add);

        assertEquals(personsTestData.size(), found.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void saveInvalidEntityServiceTest() {
        Person found = service.save(invalidPerson);

        assertEquals(personToInsert, found);
    }

    @AfterEach
    public void clearService() {
        service.clearFilter();
    }
}
