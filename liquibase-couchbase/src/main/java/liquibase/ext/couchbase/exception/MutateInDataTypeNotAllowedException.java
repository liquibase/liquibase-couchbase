package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

import static java.lang.String.format;

public class MutateInDataTypeNotAllowedException extends RuntimeException {

    private static final String template = "Data type %s is not allowed for mutateIn type %s";

    public MutateInDataTypeNotAllowedException(DataType dataType, MutateInType type) {
        super(format(template, dataType, type));
    }

}
