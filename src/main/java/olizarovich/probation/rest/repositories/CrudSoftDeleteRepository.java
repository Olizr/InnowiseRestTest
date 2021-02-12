package olizarovich.probation.rest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Repository uses Specification for searching in database
 *
 * @param <T>  Entity type
 * @param <ID> Id type
 */
@NoRepositoryBean
public interface CrudSoftDeleteRepository<T, ID> extends JpaRepository<T, ID> {
    /**
     * Methods to find specific documents
     *
     * @param specification Filter settings
     * @return Documents that matches filter
     */
    List<T> findAll(Specification<T> specification);

    /**
     * Methods to find specific documents
     *
     * @param specification Filter settings
     * @param sort          Sort order
     * @return Documents that matches filter
     */
    List<T> findAll(Specification<T> specification, Sort sort);

    /**
     * Methods to find specific documents separated by pages
     *
     * @param specification Filter settings
     * @param pageable      Page settings
     * @return Documents that matches filter
     */
    Page<T> findAll(Specification<T> specification, Pageable pageable);

    /**
     * Counts all entity that match specification
     *
     * @param specification Filter settings
     * @return Count of found entities
     */
    long count(Specification<T> specification);

    /**
     * Methods to find all documents that been soft deleted
     *
     * @return Documents with column "isDeleted" set to true
     */
    List<T> findByIsDeleted(Boolean isDeleted);
}
