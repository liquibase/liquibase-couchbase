package liquibase.ext.couchbase.validator;

import java.util.List;

import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

/**
 * Validator for insert, upsert, array_add_unique mutate in operations
 */
public class MutateInInsertUpsertUniqueValidator extends MutateInValidator {

    public MutateInInsertUpsertUniqueValidator(MutateInType mutateInType) {
        super(mutateInType);
    }

    @Override
    public void validate(String path, List<Value> values) {
        validatePathPresence(path);
        validateSingleValuePresence(values);
    }


}
