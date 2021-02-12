package olizarovich.probation.rest.services;

import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Interface for basic CRUD operations
 *
 * @param <T>  Entity type
 * @param <ID> Id type
 */
@Service
public interface Crud<T, ID> {
    T save(T entity);

    Iterable<T> saveAll(Iterable<T> entities);

    Optional<T> findById(ID id);

    boolean existsById(ID id);

    Iterable<T> findAll();

    Iterable<T> findAll(Iterable<ID> ids);

    long count();

    void deleteById(ID id);

    void delete(T entity);

    void deleteAll(Iterable<T> var1);

    void deleteAll();

    /**
     * Searching for soft deleted entities only
     *
     * @return Interface for further settings
     */
    Crud<T, ID> searchOnlyInDeleted();

    /**
     * Searching for soft deleted and not deleted entities
     *
     * @return Interface for further settings
     */
    Crud<T, ID> includeDeleted();

    /**
     * Clearing search filter
     */
    void clearFilter();
}
