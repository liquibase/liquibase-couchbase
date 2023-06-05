package liquibase.ext.couchbase.validator;

import java.util.List;

import liquibase.ext.couchbase.exception.MutateInValueNotAllowedException;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

/**
 * Validator for remove mutate in operation
 */
public class MutateInRemoveValidator extends MutateInValidator {

    public MutateInRemoveValidator(MutateInType mutateInType) {
        super(mutateInType);
    }

    @Override
    public void validate(String path, List<Value> values) {
        if (!values.isEmpty()) {
            throw new MutateInValueNotAllowedException(mutateInType);
        }
    }


}
