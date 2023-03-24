package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

public class InvalidJSONException extends RuntimeException {

    private static final String template = "The provided [%s] changelog file has invalid structure";

    public InvalidJSONException(String fileName) {
        super(format(template, fileName));
    }
}
