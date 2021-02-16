package olizarovich.probation.rest.services.implementation;

import olizarovich.probation.rest.exceptions.DocumentNotFoundException;
import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.repositories.DocumentRepository;
import olizarovich.probation.rest.services.DocumentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

/**
 * Implement DocumentService interface. Providing sorting and filter settings.
 * Providing pagination in service
 */
@Repository
public class DocumentServiceImplementation extends CrudImplementation<Document, Integer> implements DocumentService {

    public DocumentServiceImplementation(DocumentRepository repository) {
        this.repository = repository;
    }

    /**
     * Creating page, sets sort (if needed) and making pagination request
     *
     * @param page  Page number
     * @param count Entity count for one page
     * @return Page with entities
     */
    public Page<Document> toPage(int page, int count) {
        PageRequest pageRequest;
        if (sort == null)
            pageRequest = PageRequest.of(page, count);
        else
            pageRequest = PageRequest.of(page, count, sort);

        setDeletedSearch();
        Specification<Document> specification = specificationsBuilder.build();
        clearFilter();

        return repository.findAll(specification, pageRequest);
    }

    @Override
    public Document update(Document document, Integer ids) {
        boolean entityIllegal = verifyEntity(document);

        if (entityIllegal) {
            throw new IllegalArgumentException();
        }

        Document documentToUpdate = findById(ids).orElseThrow(() -> new DocumentNotFoundException(ids));

        documentToUpdate.setId(documentToUpdate.getId());

        return super.save(document);
    }

    @Override
    public DocumentService setSort(DocumentService.DocumentSort sort) {
        this.sort = Sort.by(sort.getSortOrder());
        return this;
    }

    @Override
    public DocumentService filterByTitle(String title) {
        if (!title.isEmpty())
            specificationsBuilder.with("title", "~", title);
        return this;
    }

    @Override
    public DocumentService filterByStatus(String status) {
        if (!status.isEmpty())
            specificationsBuilder.with("status", "~", status);
        return this;
    }

    @Override
    public DocumentService filterByCreationDate(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("creationDate", ":", date);
        return this;
    }

    @Override
    public DocumentService filterByCreationDateMoreThan(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("creationDate", ">", date);
        return this;
    }

    @Override
    public DocumentService filterByCreationDateLessThan(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("creationDate", "<", date);
        return this;
    }

    @Override
    public DocumentService filterByExecutionDate(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("executionDate", ":", date);
        return this;
    }

    @Override
    public DocumentService filterByExecutionDateMoreThan(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("executionDate", ">", date);
        return this;
    }

    @Override
    public DocumentService filterByExecutionDateLessThan(LocalDate date) {
        if (date != null)
            specificationsBuilder.with("executionDate", "<", date);
        return this;
    }

    @Override
    public DocumentService filterByCustomerId(int id) {
        if (id > -1)
            specificationsBuilder.with("customer.id", ":", id);
        return this;
    }

    @Override
    public DocumentService filterByCustomerFirstName(String firstName) {
        if (!firstName.isEmpty())
            specificationsBuilder.with("customer.firstName", "~", firstName);
        return this;
    }

    @Override
    public DocumentService filterByCustomerLastName(String lastName) {
        if (!lastName.isEmpty())
            specificationsBuilder.with("customer.lastName", "~", lastName);
        return this;
    }

    @Override
    public DocumentService filterByExecutorId(int id) {
        if (id > -1)
            specificationsBuilder.with("executor.id", ":", id);
        return this;
    }

    @Override
    public DocumentService filterByExecutorFirstName(String firstName) {
        if (!firstName.isEmpty())
            specificationsBuilder.with("executor.firstName", "~", firstName);
        return this;
    }

    @Override
    public DocumentService filterByExecutorLastName(String lastName) {
        if (!lastName.isEmpty())
            specificationsBuilder.with("executor.lastName", "~", lastName);
        return this;
    }

    @Override
    public boolean verifyEntity(Document document) {
        boolean entityIllegal = false;
        try {
            entityIllegal = document.getTitle().isEmpty() || entityIllegal;
            entityIllegal = document.getStatus().isEmpty() || entityIllegal;
            entityIllegal = document.getCreationDate() == null || entityIllegal;
            entityIllegal = document.getExecutionPeriod() == null || entityIllegal;
            entityIllegal = document.getCustomer().getId() == null || entityIllegal;
            entityIllegal = document.getExecutor().getId() == null || entityIllegal;
        } catch (NullPointerException ex) {
            return true;
        }

        return entityIllegal;
    }
}
