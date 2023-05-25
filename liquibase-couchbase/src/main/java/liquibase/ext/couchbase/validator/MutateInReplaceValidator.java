package liquibase.ext.couchbase.validator;

import java.util.List;

import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

/**
 * Validator for replace mutate in operation
 */
public class MutateInReplaceValidator extends MutateInValidator {

    public MutateInReplaceValidator(MutateInType mutateInType) {
        super(mutateInType);
    }

    @Override
    public void validate(String path, List<Value> values) {
        validateSingleValuePresence(values);
    }


}
