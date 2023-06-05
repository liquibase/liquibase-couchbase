package liquibase.ext.couchbase.validator;

import java.util.List;

import liquibase.ext.couchbase.exception.MutateInDataTypeNotAllowedException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

/**
 * Validator for increment and decrement mutate in operations
 */
public class MutateInLongValueValidator extends MutateInValidator {

    public MutateInLongValueValidator(MutateInType mutateInType) {
        super(mutateInType);
    }

    @Override
    public void validate(String path, List<Value> values) {
        validatePathPresence(path);
        validateSingleValuePresence(values);

        if (values.get(0).getType() != DataType.LONG) {
            throw new MutateInDataTypeNotAllowedException(values.get(0).getType(), mutateInType);
        }
    }

}
