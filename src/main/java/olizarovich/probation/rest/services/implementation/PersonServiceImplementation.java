package olizarovich.probation.rest.services.implementation;

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
    public PersonService setSort(PersonSort sort) {
        this.sort = Sort.by(sort.getSortOrder());
        return this;
    }

    @Override
    public PersonService filterByFirstName(String firstName) {
        specificationsBuilder.with("firstName", "~", firstName);
        return this;
    }

    @Override
    public PersonService filterByLastName(String lastName) {
        specificationsBuilder.with("lastName", "~", lastName);
        return this;
    }

    @Override
    public PersonService filterByBirthDate(LocalDate date) {
        specificationsBuilder.with("birthDate", ":", date);
        return this;
    }

    @Override
    public PersonService filterByBirthDateMoreThan(LocalDate date) {
        specificationsBuilder.with("birthDate", ">", date);
        return this;
    }

    @Override
    public PersonService filterByBirthDateLessThan(LocalDate date) {
        specificationsBuilder.with("birthDate", "<", date);
        return this;
    }
}
