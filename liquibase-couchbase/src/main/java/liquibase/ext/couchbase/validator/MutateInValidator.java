package liquibase.ext.couchbase.validator;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import liquibase.ext.couchbase.exception.MutateInMultipleValuesNotAllowedException;
import liquibase.ext.couchbase.exception.MutateInNoValueException;
import liquibase.ext.couchbase.exception.MutateInPathNotProvidedException;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import lombok.AllArgsConstructor;
import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Validator for mutate in operations
 */
@AllArgsConstructor
public abstract class MutateInValidator {

    protected MutateInType mutateInType;

    public abstract void validate(String path, Value value, List<Value> values);

    protected void validatePathPresence(String path) {
        if (isEmpty(path)) {
            throw new MutateInPathNotProvidedException(mutateInType);
        }
    }

    protected void validateValuePresence(Value value) {
        if (isValueEmpty(value)) {
            throw new MutateInNoValueException(mutateInType);
        }
    }

    protected boolean isValueEmpty(Value value) {
        return !Optional.ofNullable(value)
            .map(Value::getData)
            .filter(StringUtils::isNotBlank)
            .isPresent();
    }

    protected void validateNoMultipleValues(List<Value> values) {
        if (!values.isEmpty()) {
            throw new MutateInMultipleValuesNotAllowedException(mutateInType);
        }
    }

}
