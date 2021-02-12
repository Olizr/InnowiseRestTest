package olizarovich.probation.rest.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
    @GeneratedValue( strategy= GenerationType.IDENTITY)
    private Integer id;

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
     * Person type field. Contains customer
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Person customer;

    /**
     * Person type field. Contains executor
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executor_id", nullable = false)
    private Person executor;

    /**
     * Boolean type field. Field for soft delete
     */
    private Boolean isDeleted;

    /**
     * Empty class constructor.
     */
    public Document() {
        isDeleted = false;
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
        return id.equals(document.id) &&
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
