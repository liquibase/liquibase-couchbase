package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

/**
 * Indicates error during getting key by key provider
 */
public class ProvideKeyFailedException extends RuntimeException {

    private static final String template = "Can't provide key because: [%s]";

    public ProvideKeyFailedException(String msg) {
        super(format(template, msg));
    }
}
