package olizarovich.probation.rest.repositories;

import olizarovich.probation.rest.models.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Person entity with soft deletion
 */
@Repository
public interface PersonRepository extends CrudSoftDeleteRepository<Person, Integer> {
    /**
     * Soft deleted person by setting column "isDeleted" to true
     * @param id Id of a person to soft delete
     */
    @Query("update Person e set e.isDeleted=true where e.id=?1")
    @Modifying
    @Override
    void deleteById(Integer id);

    /**
     * Soft deleted person by setting column "isDeleted" to true
     * @param person Person to soft delete
     */
    @Query("update Person e set e.isDeleted=true where e.id=:#{#person.id}")
    @Modifying
    @Override
    void delete(@Param("person") Person person);

    /**
     * Soft deleted person by setting column "isDeleted" to true
     * @param entities List of persons to delete
     */
    @Modifying
    @Override
    default void deleteAll(Iterable<? extends Person> entities) {
        entities.forEach(entity -> deleteById(entity.getId()));
    }

    /**
     * Soft deleted all persons by setting column "isDeleted" to true
     */
    @Query("update Person e set e.isDeleted=true")
    @Modifying
    @Override
    void deleteAll();
}
