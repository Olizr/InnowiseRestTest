package olizarovich.probation.rest.services.implementation;

import olizarovich.probation.rest.exceptions.PersonNotFoundException;
import olizarovich.probation.rest.models.Person;
import olizarovich.probation.rest.models.Role;
import olizarovich.probation.rest.repositories.PersonRepository;
import olizarovich.probation.rest.repositories.RoleRepository;
import olizarovich.probation.rest.services.PersonService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implement PersonService interface. Providing sorting and filter settings.
 * Providing pagination in service
 */
@Service
public class PersonServiceImplementation extends CrudImplementation<Person, Integer>
        implements PersonService {

    private PasswordEncoder bCryptPasswordEncoder;
    private RoleRepository roleRepository;

    public PersonServiceImplementation() {
    }

    public PersonServiceImplementation(PasswordEncoder bCryptPasswordEncoder,
                                       RoleRepository roleRepository,
                                       PersonRepository personRepository) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
        this.repository = personRepository;
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
    public Person findByUsername(String username) {
        return ((PersonRepository)repository).findByUsername(username);
    }

    @Override
    public Person save(Person person) {
        if (verifyEntity(person)) {
            throw new IllegalArgumentException("Incorrect entity");
        }

        if (((PersonRepository)repository).findByUsername(person.getUsername()) != null) {
            throw new IllegalArgumentException("User with such username already exist");
        }

        person.setPassword(bCryptPasswordEncoder.encode(person.getPassword()));
        person.setDeleted(false);

        person = super.save(person);

        Role userRole = new Role();
        userRole.setPersonId(person.getId());
        userRole.setRole("ROLE_USER");
        roleRepository.save(userRole);

        return person;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = ((PersonRepository) repository).findByUsername(username);

        if(person == null) {
            throw new UsernameNotFoundException("Person not found");
        }

        List<GrantedAuthority> authorities = getUserAuthority(person.getRoles());

        return new User(person.getUsername(), person.getPassword(), authorities);
    }

    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        for (Role role : userRoles) {
            roles.add(new SimpleGrantedAuthority(role.getRole()));
        }
        return new ArrayList<>(roles);
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
    public PersonService filterByUsername(String username) {
        if (!username.isEmpty())
            specificationsBuilder.with("username", ":", username);
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
        try {
            entityIllegal = person.getUsername().isEmpty() || entityIllegal;
            entityIllegal = person.getPassword().isEmpty() || entityIllegal;
            entityIllegal = person.getFirstName().isEmpty() || entityIllegal;
            entityIllegal = person.getLastName().isEmpty() || entityIllegal;
            entityIllegal = person.getBirthDate() == null || entityIllegal;
        }catch (NullPointerException ex) {
            return true;
        }


        return entityIllegal;
    }
}
