package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

public class ResourceNotFoundException extends RuntimeException {

    private static final String template = "Resource by [%s] path does not exist";

    public ResourceNotFoundException(String fileName) {
        super(format(template, fileName));
    }
}
