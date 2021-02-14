package olizarovich.probation.rest.exceptions;

/**
 * Exception for failing to find document with giving id
 */
public class DocumentNotFoundException extends RuntimeException {
    private static String errorMessage = "Document with id=\"%d\" not found!";

    public DocumentNotFoundException(int id) {
        super(String.format(errorMessage, id));
    }
}
