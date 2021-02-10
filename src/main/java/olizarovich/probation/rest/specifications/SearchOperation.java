package olizarovich.probation.rest.specifications;

/**
 * Class keeping possible search operations
 */
public enum SearchOperation {
    EQUALITY, NEGATION, GREATER_THAN, LESS_THAN, LIKE;
    public static final String[] SIMPLE_OPERATION_SET =
            { ":", "!", ">", "<", "~" };

    /**
     * Method for searching operations by given strin
     * @param input Operation in string format
     * @return Enum version of operation
     */
    public static SearchOperation getSimpleOperation(String input)
    {
        switch (input) {
            case ":": return EQUALITY;
            case "!": return NEGATION;
            case ">": return GREATER_THAN;
            case "<": return LESS_THAN;
            case "~": return LIKE;
            default: return null;
        }
    }
}
