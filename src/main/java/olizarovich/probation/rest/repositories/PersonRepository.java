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

@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
    /**
     * Methods to find specific persons
     * @param specification Filter settings
     * @return Persons that matches filter
     */
    List<Person> findAll(Specification<Person> specification);

    /**
     * Methods to find specific persons separated by pages
     * @param specification Filter settings
     * @param pageable Page settings
     * @return Persons that matches filter
     */
    Page<Person> findAll(Specification<Person> specification, Pageable pageable);

    /**
     * Methods to find all person that been soft deleted
     * @return Persons with column "isDeleted" set to true
     */
    @Query("select e from Person e where e.isDeleted=true")
    List<Person> recycleBin();

    /**
     * Soft deleted person by setting column "isDeleted" to true
     * @param id Id of a person to soft delete
     */
    @Query("update Person e set e.isDeleted=true where e.id=?1")
    @Modifying
    void softDeleteById(int id);

    /**
     * Soft deleted person by setting column "isDeleted" to true
     * @param person Person to soft delete
     */
    @Query("update Person e set e.isDeleted=true where e.id=:#{#person.id}")
    @Modifying
    void softDelete(@Param("person") Person person);

    /**
     * Soft deleted person by setting column "isDeleted" to true
     * @param personsIds List of Integer of persons to delete
     */
    @Query("update Person e set e.isDeleted=true where e.id IN :ids")
    @Modifying
    void softDeleteAll(@Param("ids") Collection<Integer> personsIds);
}
