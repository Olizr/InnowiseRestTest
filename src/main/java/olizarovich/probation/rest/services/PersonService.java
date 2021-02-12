package olizarovich.probation.rest.services;

import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.services.implementation.PersonServiceImplementation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface PersonService extends Crud<Person, Integer>{
    Page<Person> toPage(int page, int count);

    PersonService setSort(PersonService.PersonSort sort);

    PersonService filterByFirstName(String firstName);
    PersonService filterByLastName(String lastName);

    PersonService filterByBirthDate(LocalDate date);
    PersonService filterByBirthDateMoreThan(LocalDate date);
    PersonService filterByBirthDateLessThan(LocalDate date);

    /**
     * Sorting option for Person entity
     */
    enum PersonSort {
        FIRSTNAME("firstName"), LASTNAME("lastName"), BIRTHDATE("birthDate");

        private String sortOrder;
        PersonSort(String code){
            this.sortOrder = code;
        }

        public String getSortOrder(){ return sortOrder;}
    }
}
