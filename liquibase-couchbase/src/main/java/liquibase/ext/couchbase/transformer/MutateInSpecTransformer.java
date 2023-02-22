package liquibase.ext.couchbase.transformer;

import com.couchbase.client.java.kv.MutateInSpec;
import com.google.common.collect.Sets;
import liquibase.ext.couchbase.exception.MutateInDataTypeNotAllowedException;
import liquibase.ext.couchbase.exception.MutateInNoValueException;
import liquibase.ext.couchbase.exception.MutateInValuesNotAllowedException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateInType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_APPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_CREATE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_INSERT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_PREPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.DECREMENT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.INCREMENT;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Class for validating {@link LiquibaseMutateInSpec} and transforming it to {@link MutateInSpec}
 */
public class MutateInSpecTransformer {

    private static final Set<MutateInType> multipleValueMutateInTypes = Sets.newHashSet(ARRAY_PREPEND, ARRAY_APPEND,
            ARRAY_CREATE, ARRAY_INSERT);
    private static final Set<MutateInType> longTypes = Sets.newHashSet(INCREMENT, DECREMENT);

    public MutateInSpec toSpec(LiquibaseMutateInSpec liquibaseMutateInSpec) {
        validate(liquibaseMutateInSpec);

        String path = liquibaseMutateInSpec.getPath();
        Value value = liquibaseMutateInSpec.getValue();
        List<Value> values = liquibaseMutateInSpec.getValues();
        MutateInType mutateInType = liquibaseMutateInSpec.getMutateInType();

        if (multipleValueMutateInTypes.contains(mutateInType)) {
            return toMultipleValueSpec(path, value, values, mutateInType);
        }

        return mutateInType.toMutateInSpec(path, value.mapDataToType());
    }

    private void validate(LiquibaseMutateInSpec liquibaseMutateInSpec) {
        Value value = liquibaseMutateInSpec.getValue();
        List<Value> values = liquibaseMutateInSpec.getValues();
        MutateInType mutateInType = liquibaseMutateInSpec.getMutateInType();

        if ((value == null || isEmpty(value.getData())) && (values.isEmpty())) {
            throw new MutateInNoValueException(liquibaseMutateInSpec.getMutateInType());
        }
        if (!values.isEmpty() && !multipleValueMutateInTypes.contains(mutateInType)) {
            throw new MutateInValuesNotAllowedException(mutateInType);
        }
        if (longTypes.contains(mutateInType) && value.getType() != DataType.LONG) {
            throw new MutateInDataTypeNotAllowedException(value.getType(), mutateInType);
        }
    }

    private MutateInSpec toMultipleValueSpec(String path, Value value, List<Value> values, MutateInType mutateInType) {
        List<Object> valuesToSave = new ArrayList<>();

        if (!values.isEmpty()) {
            values.stream().filter(valueToSave -> isNotEmpty(valueToSave.getData())).map(Value::mapDataToType).forEach(
                    valuesToSave::add);
        }
        else {
            valuesToSave.add(value.mapDataToType());
        }
        return mutateInType.toMutateInSpec(path, valuesToSave);
    }

}
