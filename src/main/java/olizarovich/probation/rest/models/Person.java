package olizarovich.probation.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

/**
 * The Person class is data class for holding information
 */
@Entity
@Table(name = "persons")
public class Person {

    /**
     * Integer field. Contains primary key in class
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * String field. Contains username for security
     */
    private String username;

    /**
     * String field. Contains password for security
     */
    @JsonIgnore
    private String password;

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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "personId")
    private Set<Role> roles;

    /**
     * Boolean type field. Field for soft delete
     */
    @JsonIgnore
    private Boolean isDeleted;

    /**
     * Empty class constructor.
     */
    public Person() {
        this.isDeleted = false;
    }

    /**
     * Class constructor with all fields
     *
     * @param firstName String first name
     * @param lastName  String last name
     * @param birthDate LocalDate birth date
     */
    public Person(String username, String password, String firstName,
                  String lastName, LocalDate birthDate) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.isDeleted = false;
    }

    /**
     * Uses id, first name and last name to compare objects
     *
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
     *
     * @return Object hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public Boolean getDeleted() {
        return isDeleted;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
