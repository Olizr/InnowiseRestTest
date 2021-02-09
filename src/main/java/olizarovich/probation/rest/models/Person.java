package olizarovich.probation.rest.models;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
* The Person class is data class for holding information
*/
@Entity
public class Person {

    /**
     * Integer field. Contains primary key in class
     */
    @Id
    @GeneratedValue( strategy= GenerationType.IDENTITY)
    private int id;

    /**
     * String field. Contains person first name
     */
    private String firstName;

    /**
     * String field. Contains person last name
     */
    private String lastName;

    /**
     * LocalDate field. Contains person birth date
     * Format "yyyy-MM-dd"
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;

    /**
     * Boolean type field. Field for soft delete
     */
    private boolean isDeleted;

    /**
     * Empty class constructor.
     */
    public Person() {
        this.isDeleted = false;
    }

    /**
     * Class constructor with all fields
     * @param firstName String first name
     * @param lastName String last name
     * @param birthDate LocalDate birth date
     */
    public Person(String firstName, String lastName, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.isDeleted = false;
    }

    /**
     * Uses id, first name and last name to compare objects
     * @param o Object to compare with
     * @return True if equals, false in other case
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(id, person.id) &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName);
    }

    /**
     * Uses id, first name and last name to create hash
     * @return Object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
