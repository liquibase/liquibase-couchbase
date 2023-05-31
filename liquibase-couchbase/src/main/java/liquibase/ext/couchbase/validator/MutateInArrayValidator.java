package liquibase.ext.couchbase.validator;

import java.util.List;

import liquibase.ext.couchbase.exception.MutateInNoValueException;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

/**
 * Validator for array append, prepend, insert, create mutate in operations
 */
public class MutateInArrayValidator extends MutateInValidator {

    public MutateInArrayValidator(MutateInType mutateInType) {
        super(mutateInType);
    }

    @Override
    public void validate(String path, List<Value> values) {
        validatePathPresence(path);
        if (values.isEmpty()) {
            throw new MutateInNoValueException(mutateInType);
        }
    }

}
