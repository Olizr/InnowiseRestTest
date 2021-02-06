package olizarovich.probation.rest.flyway.postqresql;

import olizarovich.probation.rest.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Page<Person> findAll(Pageable pageable);

    Page<Person> findAll(Sort sort);

    Page<Person> findAll(Specification<Person> filter);

    Page<Person> findAll(Pageable pageable, Specification<Person> filter);

    Page<Person> findAll(Pageable pageable, Sort sort);

    Page<Person> findAll(Specification<Person> filter, Sort sort);

    Page<Person> findAll(Pageable pageable, Specification<Person> filter, Sort sort);

    @Override
    <S extends Person> S save(S s);

    @Override
    <S extends Person> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    Optional<Person> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);

    @Override
    Iterable<Person> findAll();

    @Override
    Iterable<Person> findAllById(Iterable<Long> iterable);

    @Override
    long count();

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(Person person);

    @Override
    void deleteAll(Iterable<? extends Person> iterable);

    @Override
    void deleteAll();
}
