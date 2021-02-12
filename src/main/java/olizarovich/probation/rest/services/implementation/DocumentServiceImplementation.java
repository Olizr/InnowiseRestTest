package olizarovich.probation.rest.services.implementation;

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
     * @param page Page number
     * @param count Entity count for one page
     * @return Page with entities
     */
    public Page<Document> toPage(int page, int count) {
        PageRequest pageRequest;
        if (sort == null)
            pageRequest = PageRequest.of(page, count);
        else
            pageRequest = PageRequest.of(page, count, sort);

        Specification<Document> specification = specificationsBuilder.build();
        clearFilter();

        return repository.findAll(specification, pageRequest);
    }

    @Override
    public DocumentService setSort(DocumentService.DocumentSort sort) {
        this.sort = Sort.by(sort.getSortOrder());
        return this;
    }

    @Override
    public DocumentService filterByTitle(String title) {
        specificationsBuilder.with("title", "~", title);
        return this;
    }

    @Override
    public DocumentService filterByStatus(String status) {
        specificationsBuilder.with("status", "~", status);
        return null;
    }

    @Override
    public DocumentService filterByCreationDate(LocalDate date) {
        specificationsBuilder.with("creationDate", ":", date);
        return null;
    }

    @Override
    public DocumentService filterByCreationDateMoreThan(LocalDate date) {
        specificationsBuilder.with("creationDate", ">", date);
        return null;
    }

    @Override
    public DocumentService filterByCreationDateLessThan(LocalDate date) {
        specificationsBuilder.with("creationDate", "<", date);
        return null;
    }

    @Override
    public DocumentService filterByExecutionDate(LocalDate date) {
        specificationsBuilder.with("executionDate", ":", date);
        return null;
    }

    @Override
    public DocumentService filterByExecutionDateMoreThan(LocalDate date) {
        specificationsBuilder.with("executionDate", ">", date);
        return null;
    }

    @Override
    public DocumentService filterByExecutionDateLessThan(LocalDate date) {
        specificationsBuilder.with("executionDate", "<", date);
        return null;
    }

    @Override
    public DocumentService filterByCustomerId(int id) {
        specificationsBuilder.with("customer.id", ":", id);
        return null;
    }

    @Override
    public DocumentService filterByCustomerFirstName(String firstName) {
        specificationsBuilder.with("customer.firstName", "~", firstName);
        return null;
    }

    @Override
    public DocumentService filterByCustomerLastName(String lastName) {
        specificationsBuilder.with("customer.lastName", "~", lastName);
        return null;
    }

    @Override
    public DocumentService filterByExecutorId(int id) {
        specificationsBuilder.with("executor.id", ":", id);
        return null;
    }

    @Override
    public DocumentService filterByExecutorFirstName(String firstName) {
        specificationsBuilder.with("executor.firstName", "~", firstName);

        return null;
    }

    @Override
    public DocumentService filterByExecutorLastName(String lastName) {
        specificationsBuilder.with("executor.lastName", "~", lastName);
        return null;
    }
}
