package olizarovich.probation.rest.test.repositories;

import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.specifications.SpecificationsBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Class for testing PersonRepository using h2 in memory database
 */
@RunWith(SpringRunner.class)
@DataJpaTest
public class PersonRepositoryIntegrationTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Creates 5 persons entities
     *
     * @return List of persons
     */
    private List<Person> initTestData() {
        List<Person> persons = new ArrayList<>();
        LocalDate localDate = LocalDate.of(2000, 1, 1);
        for (int i = 0; i < 5; i++) {
            persons.add(new Person("alex" + i, "xela" + i, localDate));
        }

        return persons;
    }

    /**
     * Add list of persons into database
     *
     * @param persons List of persons to add into database
     */
    private void initDatabase(List<Person> persons) {
        for (Person i : persons) {
            entityManager.persist(i);
            entityManager.flush();
        }
    }

    /**
     * Testing findAll repository methods
     */
    @Test
    public void testReadDataFromDatabase() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        List<Person> found = personRepository.findAll();

        assertEquals(found.size(), persons.size());
    }

    /**
     * Testing findAll repository methods with pagination
     */
    @Test
    public void testPageFilter() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        Pageable pageable = PageRequest.of(0, 2);

        List<Person> found = personRepository.findAll(pageable).toList();

        assertEquals(found.size(), 2);
    }

    /**
     * Testing findAll repository methods with equality filter
     */
    @Test
    public void testEqualFilter() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        SpecificationsBuilder<Person> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("firstName", ":", "alex2");

        List<Person> found = personRepository.findAll(specificationsBuilder.build());

        assertEquals(1, found.size());
    }

    /**
     * Testing findAll repository methods with equality filter and pagination
     */
    @Test
    public void testEqualFilterWithPage() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        Pageable pageable = PageRequest.of(0, 2);
        SpecificationsBuilder<Person> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("firstName", "~", "alex");

        List<Person> found = personRepository.findAll(specificationsBuilder.build(), pageable).toList();

        assertEquals(2, found.size());
    }

    /**
     * Testing save method. Should return all entity that was saved
     */
    @Test
    public void testSaveDataToDatabase() {
        List<Person> persons = initTestData();
        for (Person i : persons) {
            personRepository.save(i);
        }

        List<Person> found = personRepository.findAll();

        assertEquals(found.size(), persons.size());
    }

    /**
     * Testing update method. Changes firstName of entity.
     * And checking if changes applied.
     */
    @Test
    public void testUpdateEntity() {
        String newName = "Bosch";

        List<Person> persons = initTestData();
        initDatabase(persons);

        int personId = persons.get(0).getId();
        Person person = personRepository.findById(personId).get();


        person.setFirstName(newName);
        personRepository.save(person);

        person = personRepository.getOne(personId);
        assertEquals(newName, person.getFirstName());
    }

    /**
     * Testing softDelete to set isDeleted field instead of delete row
     * Soft delete by Id
     */
    @Test
    public void testSoftDeleteById() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        SpecificationsBuilder<Person> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("firstName", ":", "alex2");

        Person person = personRepository.findAll(specificationsBuilder.build()).get(0);

        personRepository.deleteById(person.getId());

        assertEquals(1, personRepository.findByIsDeleted(true).size());
    }

    /**
     * Testing softDelete to set isDeleted field instead of delete row
     * Soft delete by entity
     */
    @Test
    public void testSoftDelete() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        Person person = personRepository.findById(persons.get(0).getId()).get();

        personRepository.delete(person);

        assertEquals(1, personRepository.findByIsDeleted(true).size());
    }

    /**
     * Testing softDelete to set isDeleted field instead of delete row
     * Soft delete on list of personIds
     */
    @Test
    public void testSoftDeleteAll() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        List<Person> personsToDelete = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            personsToDelete.add(persons.get(i));
        }

        personRepository.deleteAll(personsToDelete);

        assertEquals(3, personRepository.findByIsDeleted(true).size());
    }

    @Test
    public void testCountWithFilter() {
        List<Person> persons = initTestData();
        initDatabase(persons);

        SpecificationsBuilder<Person> specificationsBuilder = new SpecificationsBuilder<>();
        specificationsBuilder.with("firstName", ":", "alex2");
        long found = personRepository.count(specificationsBuilder.build());

        assertEquals(1, found);
    }
}
