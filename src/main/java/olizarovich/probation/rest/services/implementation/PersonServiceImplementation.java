package olizarovich.probation.rest.services.implementation;

import olizarovich.probation.rest.exceptions.PersonNotFoundException;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.services.PersonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Implement PersonService interface. Providing sorting and filter settings.
 * Providing pagination in service
 */
@Service
public class PersonServiceImplementation extends CrudImplementation<Person, Integer>
        implements PersonService {

    public PersonServiceImplementation(PersonRepository repository) {
        this.repository = repository;
    }

    /**
     * Creating page, sets sort (if needed) and making pagination request
     * @param page Page number
     * @param count Entity count for one page
     * @return Page with entities
     */
    @Override
    public Page<Person> toPage(int page, int count) {
        PageRequest pageRequest;
        if (sort == null)
            pageRequest = PageRequest.of(page, count);
        else
            pageRequest = PageRequest.of(page, count, sort);

        setDeletedSearch();
        Specification<Person> specification = specificationsBuilder.build();
        clearFilter();
        return repository.findAll(specification, pageRequest);
    }

    @Override
    public Person update(Person person, Integer ids) {
        boolean entityIllegal = verifyEntity(person);

        if(entityIllegal) {
            throw new IllegalArgumentException();
        }

        Person personToUpdate = findById(ids).orElseThrow(() -> new PersonNotFoundException(ids));

        person.setId(personToUpdate.getId());

        return super.save(person);
    }

    @Override
    public PersonService setSort(PersonSort sort) {
        this.sort = Sort.by(sort.getSortOrder());
        return this;
    }

    @Override
    public PersonService filterByFirstName(String firstName) {
        if (!firstName.isEmpty())
            specificationsBuilder.with("firstName", "~", firstName);
        return this;
    }

    @Override
    public PersonService filterByLastName(String lastName) {
        if (!lastName.isEmpty())
            specificationsBuilder.with("lastName", "~", lastName);
        return this;
    }

    @Override
    public PersonService filterByBirthDate(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("birthDate", ":", date);
        return this;
    }

    @Override
    public PersonService filterByBirthDateMoreThan(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("birthDate", ">", date);
        return this;
    }

    @Override
    public PersonService filterByBirthDateLessThan(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("birthDate", "<", date);
        return this;
    }

    /**
     * Verifying fileds
     * @return True if all field correct
     */
    public boolean verifyEntity(Person person) {
        boolean entityIllegal = false;
        entityIllegal = person.getFirstName().isEmpty() || entityIllegal;
        entityIllegal = person.getLastName().isEmpty() || entityIllegal;
        entityIllegal = person.getBirthDate() == null || entityIllegal;

        return entityIllegal;
    }
}
