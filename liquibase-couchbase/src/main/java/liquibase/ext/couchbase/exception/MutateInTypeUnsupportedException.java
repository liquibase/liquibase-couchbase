package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.DataType;

import static java.lang.String.format;

public class MutateInTypeUnsupportedException extends RuntimeException {

    private static final String message = "%s type is not supported in mutateIn";

    public MutateInTypeUnsupportedException(DataType type) {
        super(format(message, type));
    }
}
