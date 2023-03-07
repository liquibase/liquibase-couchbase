package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.subdoc.MutateInType;

import static java.lang.String.format;

public class MutateInValueNotAllowedException extends RuntimeException {

    private static final String template = "Value(s) not allowed in %s mutateIn type";

    public MutateInValueNotAllowedException(MutateInType type) {
        super(format(template, type));
    }
}
