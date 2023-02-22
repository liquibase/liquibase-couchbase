package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.subdoc.MutateInType;

import static java.lang.String.format;

public class MutateInValuesNotAllowedException extends RuntimeException {

    private static final String template = "More than one values are not allowed in %s mutateIn type.";

    public MutateInValuesNotAllowedException(MutateInType type) {
        super(format(template, type));
    }
}
