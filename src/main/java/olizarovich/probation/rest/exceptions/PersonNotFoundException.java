package olizarovich.probation.rest.exceptions;

/**
 * Exception for failing to find person with giving id
 */
public class PersonNotFoundException extends RuntimeException {
    private static String errorMessage = "Person with id=\"%d\" not found!";

    public PersonNotFoundException(int id) {
        super(String.format(errorMessage, id));
    }
}
