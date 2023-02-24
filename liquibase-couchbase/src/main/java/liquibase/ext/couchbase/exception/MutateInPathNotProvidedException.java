package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.subdoc.MutateInType;

import static java.lang.String.format;

public class MutateInPathNotProvidedException extends RuntimeException {

    private static final String message = "Path is not provided for mutateIn type %s";

    public MutateInPathNotProvidedException(MutateInType type) {
        super(format(message, type));
    }

}
