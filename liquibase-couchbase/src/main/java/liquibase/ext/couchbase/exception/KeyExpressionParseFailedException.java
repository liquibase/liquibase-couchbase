package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

/**
 * Indicates error during parsing key expression
 */
public class KeyExpressionParseFailedException extends RuntimeException {

    private static final String template = "Key expression parse failed: [%s]";

    public KeyExpressionParseFailedException(String details) {
        super(format(template, details));
    }
}
