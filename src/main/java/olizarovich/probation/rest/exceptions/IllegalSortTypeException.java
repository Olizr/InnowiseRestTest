package olizarovich.probation.rest.exceptions;

/**
 * Exception for wrong sort type.
 */
public class IllegalSortTypeException extends RuntimeException {
    private static String message = "Sort type \"%s\" not found!";

    public IllegalSortTypeException(String errorMessage) {
        super(String.format(message, errorMessage));
    }
}
