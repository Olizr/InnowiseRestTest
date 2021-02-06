package olizarovich.probation.rest.flyway.postqresql;

import olizarovich.probation.rest.models.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

    Iterable<Document> findAll(Sort sort);

    Iterable<Document> findAll(Specification<Document> filter);

    Page<Document> findAll(Sort sort, Specification<Document> filter);

    Page<Document> findAll(Pageable pageable);

    Page<Document> findAll(Pageable pageable, Sort sort);

    Page<Document> findAll(Pageable pageable, Specification<Document> filter);

    Page<Document> findByCustomerId(Long customerId, Sort sort);

    Page<Document> findByCustomerId(Long customerId, Pageable pageable);

    Page<Document> findByCustomerId(Long customerId, Pageable pageable, Sort sort);

    Page<Document> findByCustomerId_Lastname(String lastname, Sort sort);

    Page<Document> findByCustomerId_Lastname(Long lastname, Pageable pageable);

    Page<Document> findByCustomerId_Lastname(Long lastname, Pageable pageable, Sort sort);

    @Override
    <S extends Document> S save(S s);

    @Override
    void deleteAll();

    @Override
    <S extends Document> Iterable<S> saveAll(Iterable<S> iterable);

    @Override
    Optional<Document> findById(Long aLong);

    @Override
    boolean existsById(Long aLong);

    @Override
    Iterable<Document> findAll();

    @Override
    Iterable<Document> findAllById(Iterable<Long> iterable);

    @Override
    long count();

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(Document document);

    @Override
    void deleteAll(Iterable<? extends Document> iterable);
}
