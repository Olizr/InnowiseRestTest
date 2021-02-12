package olizarovich.probation.rest.test.services;

import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.services.PersonService;
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

/**
 * Class for testing service layer
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonServiceTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonService service;

    /**
     * Create data and loads it into database
     */
    public List<Person> initData() {
        List<Person> persons = DataInit.createPersonsData(5);
        loadToDatabase(persons);
        return persons;
    }

    /**
     * Add list of persons into database
     *
     * @param persons List of persons to add into database
     */
    private void loadToDatabase(List<Person> persons) {
        for (Person i : persons) {
            entityManager.persist(i);
            entityManager.flush();
        }
    }

    @Test
    public void findAllServiceTest() {
        List<Person> persons = initData();
        List<Person> found = new ArrayList<>();
        service.findAll().forEach(found::add);

        assertEquals(persons.size(), found.size());
    }

    @Test
    public void findAllWithSortServiceTest() {
        List<Person> persons = initData();
        List<Person> found = new ArrayList<>();

        service.setSort(PersonService.PersonSort.FIRSTNAME).findAll().forEach(found::add);
        persons = persons.stream().sorted(Comparator.comparing(Person::getFirstName)).collect(Collectors.toList());

        assertEquals(persons.size(), found.size());
    }

    /**
     * Testing service pagination
     */
    @Test
    public void getPageServiceTest() {
        int pageSize = 2;
        List<Person> persons = initData();
        List<Person> found = new ArrayList<>();

        service.toPage(0, pageSize).forEach(found::add);

        assertEquals(pageSize, found.size());
    }

    @Test
    public void findAllWithFilterServiceTest() {
        List<Person> persons = initData();
        List<Person> found = new ArrayList<>();
        String nameToFind = persons.get(1).getFirstName();

        service.filterByFirstName(nameToFind);
        service.findAll().forEach(found::add);

        persons = persons.stream().filter(p -> p.getFirstName().contains(nameToFind)).collect(Collectors.toList());
        assertEquals(persons.size(), found.size());
    }

    @Test
    public void countWithFilterServiceTest() {
        List<Person> persons = initData();
        String nameToFind = persons.get(1).getFirstName();

        service.filterByFirstName(nameToFind);

        persons = persons.stream().filter(p -> p.getFirstName().contains(nameToFind)).collect(Collectors.toList());

        assertEquals(persons.size(), service.count());
    }

    @Test
    public void getByIdServiceTest() {
        List<Person> persons = initData();
        Person toFind = persons.get(2);
        Person found = service.findById(toFind.getId()).get();

        assertEquals(toFind, found);
    }

    @Test
    public void saveEntityServiceTest() {
        List<Person> persons = initData();
        List<Person> toAdd = DataInit.createPersonsData(2);

        service.saveAll(toAdd);

        List<Person> found = new ArrayList<>();
        service.findAll().forEach(found::add);

        assertNotEquals(persons.size(), found.size());
    }

    @Test
    public void existsByIdServiceTest() {
        List<Person> persons = initData();
        Person toFind = persons.get(2);

        assertTrue(service.existsById(toFind.getId()));

        toFind = persons.get(persons.size() - 1);
        assertFalse(service.existsById(toFind.getId() + 10));
    }

    @Test
    public void deleteByIdServiceTest() {
        List<Person> persons = initData();
        List<Person> found = new ArrayList<>();
        Person toDelete = persons.get(2);

        service.deleteById(toDelete.getId());

        service.searchOnlyInDeleted().findAll().forEach(found::add);

        assertEquals(1, found.size());
    }

    /**
     * Testing deletion using persons list.
     * Searching entity that marked with idDeleted == true
     */
    @Test
    public void deleteAllAndSearchInDeletedServiceTest() {
        List<Person> persons = initData();
        List<Person> toDelete = new ArrayList<>();

        for (int i = 0; i < persons.size() / 2; i++) {
            toDelete.add(persons.get(i));
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
