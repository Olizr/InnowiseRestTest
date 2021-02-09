package olizarovich.probation.rest.repositories;

import olizarovich.probation.rest.models.Document;
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
public interface DocumentRepository extends JpaRepository<Document, Integer> {
    /**
     * Methods to find specific documents
     * @param specification Filter settings
     * @return Documents that matches filter
     */
    List<Document> findAll(Specification<Document> specification);

    /**
     * Methods to find specific documents separated by pages
     * @param specification Filter settings
     * @param pageable Page settings
     * @return Documents that matches filter
     */
    Page<Document> findAll(Specification<Document> specification, Pageable pageable);

    /**
     * Methods to find all documents that been soft deleted
     * @return Documents with column "isDeleted" set to true
     */
    @Query("select e from Document e where e.isDeleted=true")
    List<Document> recycleBin();

    /**
     * Soft deleted documents by setting column "isDeleted" to true
     * @param id Id of a document to soft delete
     */
    @Query("update Document e set e.isDeleted=true where e.id=?1")
    @Modifying
    void softDeleteById(int id);

    /**
     * Soft deleted document by setting column "isDeleted" to true
     * @param document Document to soft delete
     */
    @Query("update Document e set e.isDeleted=true where e.id=:#{#document.id}")
    @Modifying
    void softDelete(@Param("document") Document document);

    /**
     * Soft deleted document by setting column "isDeleted" to true
     * @param documentIds List of Integer of documents to delete
     */
    @Query("update Document e set e.isDeleted=true where e.id IN :ids")
    @Modifying
    void softDeleteAll(@Param("ids") Collection<Integer> documentIds);
}
