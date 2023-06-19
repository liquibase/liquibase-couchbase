package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.KeyProviderType;

import static java.lang.String.format;

/**
 * Indicates if no key provider by type was found
 */
public class KeyProviderNotFoundException extends RuntimeException {

    private static final String template = "Key provider [%s] not found";

    public KeyProviderNotFoundException(String type) {
        super(format(template, type));
    }

    public KeyProviderNotFoundException(KeyProviderType type) {
        super(format(template, type));
    }
}
