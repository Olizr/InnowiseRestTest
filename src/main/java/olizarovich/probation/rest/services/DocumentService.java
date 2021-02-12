package olizarovich.probation.rest.services;

import olizarovich.probation.rest.models.Document;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public interface DocumentService extends Crud<Document, Integer> {
    Page<Document> toPage(int page, int count);

    DocumentService setSort(DocumentService.DocumentSort sort);

    DocumentService filterByTitle(String title);
    DocumentService filterByStatus(String status);

    DocumentService filterByCreationDate(LocalDate date);
    DocumentService filterByCreationDateMoreThan(LocalDate date);
    DocumentService filterByCreationDateLessThan(LocalDate date);

    DocumentService filterByExecutionDate(LocalDate date);
    DocumentService filterByExecutionDateMoreThan(LocalDate date);
    DocumentService filterByExecutionDateLessThan(LocalDate date);

    DocumentService filterByCustomerId(int id);
    DocumentService filterByCustomerFirstName(String firstName);
    DocumentService filterByCustomerLastName(String lastName);

    DocumentService filterByExecutorId(int id);
    DocumentService filterByExecutorFirstName(String firstName);
    DocumentService filterByExecutorLastName(String lastName);

    /**
     * Sorting option for Document entity
     */
    enum DocumentSort {
        TITLE("title"), STATUS("status"), CREATIONDATE("creationDate"), EXECUTIONPERIOD("executionPeriod"),
        CUSTOMERFIRSTNAME("customer.firstName"), CUSTOMERLASTNAME("customer.lastName"),
        EXECUTORFIRSTNAME("executor.firstName"), EXECUTORLASTNAME("executor.lastName");

        private String sortOrder;
        DocumentSort(String code){
            this.sortOrder = code;
        }

        public String getSortOrder(){ return sortOrder;}
    }
}
