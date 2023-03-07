package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.subdoc.MutateInType;

import static java.lang.String.format;

public class MutateInNoValueException extends RuntimeException {

    private static final String template = "%s mutateIn type requires value(s).";

    public MutateInNoValueException(MutateInType type) {
        super(format(template, type));
    }
}
