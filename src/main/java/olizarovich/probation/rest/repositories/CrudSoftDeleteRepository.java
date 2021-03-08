package olizarovich.probation.rest.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * Repository uses Specification for searching in database
 *
 * @param <T>  Entity type
 * @param <ID> Id type
 */
@NoRepositoryBean
public interface CrudSoftDeleteRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}
