package olizarovich.probation.rest.services.implementation;

import olizarovich.probation.rest.repositories.CrudSoftDeleteRepository;
import olizarovich.probation.rest.services.Crud;
import olizarovich.probation.rest.specifications.SpecificationsBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Abstract class. Implementing base CRUD operations
 *
 * @param <T>  Entity type
 * @param <ID> Id type
 */
@Service
public abstract class CrudImplementation<T, ID> implements Crud<T, ID> {
    /**
     * Repository with soft deletion
     */
    protected CrudSoftDeleteRepository<T, ID> repository;

    /**
     * Contains sort type. If null not used if search.
     */
    protected Sort sort = null;

    /**
     * Flag for searching softDeleted entities.
     * If false searching only not deleted entities.
     * If true searching only deleted entities.
     * If null searching both not deleted and deleted entities.
     */
    protected Boolean searchForDeleted = false;

    /**
     * Contains filter for search query.
     */
    protected SpecificationsBuilder<T> specificationsBuilder = new SpecificationsBuilder<>();

    @Override
    public T save(T entity) {
        if (verifyEntity(entity)) {
            throw new IllegalArgumentException();
        }

        return repository.save(entity);
    }

    @Override
    public Iterable<T> saveAll(Iterable<T> entities) {
        for (T i : entities) {
            if (verifyEntity(i)) {
                throw new IllegalArgumentException();
            }
        }

        return repository.saveAll(entities);
    }

    @Override
    public Optional<T> findById(ID id) {
        return repository.findById(id);
    }

    @Override
    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    /**
     * Searching entities. Building filter and uses sort (if needed).
     *
     * @return All entities in database that matches filter
     */
    @Override
    public Iterable<T> findAll() {
        setDeletedSearch();
        Specification<T> specification = specificationsBuilder.build();
        clearFilter();

        if (sort == null)
            return repository.findAll(specification);
        else
            return repository.findAll(specification, sort);
    }

    @Override
    public Iterable<T> findAll(Iterable<ID> ids) {
        return repository.findAllById(ids);
    }

    /**
     * Counting entities. Building filter and uses it.
     *
     * @return Number of entities in database that matches filter
     */
    @Override
    public long count() {
        setDeletedSearch();
        Specification<T> specification = specificationsBuilder.build();
        clearFilter();
        return repository.count(specification);
    }

    @Override
    public void deleteById(ID id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteAll(Iterable<T> entities) {
        repository.deleteAll(entities);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public Crud<T, ID> searchOnlyInDeleted() {
        searchForDeleted = true;
        return this;
    }

    @Override
    public Crud<T, ID> includeDeleted() {
        searchForDeleted = null;
        return this;
    }

    /**
     * Resets all filter and sort settings
     */
    @Override
    public void clearFilter() {
        specificationsBuilder = new SpecificationsBuilder<>();
        sort = null;
        searchForDeleted = false;
    }

    @Override
    public T update(T entity, ID ids) {
        boolean entityIllegal = verifyEntity(entity);

        if (entityIllegal) {
            throw new IllegalArgumentException();
        }

        return save(entity);
    }

    /**
     * Setting isDeleted flag to specification.
     * If false searching only not deleted entities.
     * If true searching only deleted entities.
     * If null searching both not deleted and deleted entities.
     */
    protected void setDeletedSearch() {
        if (searchForDeleted != null) {
            specificationsBuilder.with("isDeleted", ":", searchForDeleted);
        }
    }

    public abstract boolean verifyEntity(T entity);
}
