package olizarovich.probation.rest.models;

import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.Objects;

/**
 * The Document class is data class for holding information
 */
@Entity
public class Document {

    /**
     * Integer field. Contains primary key in class
     */
    @Id
    private int id;

    /**
     * String field. Contains title of the document
     */
    private String title;

    /**
     * String field. Contains status of the document
     */
    private String status;

    /**
     * LocalDate field. Contains creation date of the document
     * Format "yyyy-MM-dd"
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate creationDate;

    /**
     * LocalDate field. Contains deadline of the document
     * Format "yyyy-MM-dd"
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate executionPeriod;

    /**
     * Integer field. Contains foreign key for customer
     */
    private int customerId;

    /**
     * Person type field. Contains customer
     */
    private Person customer;

    /**
     * Integer field. Contains foreign key for executor
     */
    private int executorId;

    /**
     * Person type field. Contains executo
     */
    private Person executor;

    /**
     * Empty class constructor.
     */
    public Document() {

    }

    /**
     * Class constructor with all fields, except customer and executor
     */
    public Document(int id, String title, String status, LocalDate creationDate, LocalDate executionPeriod, int customerId, int executorId) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.creationDate = creationDate;
        this.executionPeriod = executionPeriod;
        this.customerId = customerId;
        this.executorId = executorId;
    }

    /**
     * Uses id, document title, creationDate to compare objects
     * @param o Object to compare with
     * @return True if equals, false in other case
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Document document = (Document) o;
        return id == document.id &&
                Objects.equals(title, document.title) &&
                Objects.equals(creationDate, document.creationDate);
    }

    /**
     * Uses id, document title, creationDate to create hash
     * @return Object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, creationDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getExecutionPeriod() {
        return executionPeriod;
    }

    public void setExecutionPeriod(LocalDate executionPeriod) {
        this.executionPeriod = executionPeriod;
    }

    public Person getCustomer() {
        return customer;
    }

    public void setCustomer(Person customer) {
        this.customer = customer;
    }

    public Person getExecutor() {
        return executor;
    }

    public void setExecutor(Person executor) {
        this.executor = executor;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getExecutorId() {
        return executorId;
    }

    public void setExecutorId(int executorId) {
        this.executorId = executorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
