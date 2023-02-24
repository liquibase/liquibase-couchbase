package liquibase.ext.couchbase.transformer;

import com.couchbase.client.java.kv.MutateInSpec;
import com.google.common.collect.Sets;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import liquibase.Scope;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import liquibase.ext.couchbase.validator.MutateInValidator;
import liquibase.ext.couchbase.validator.MutateInValidatorRegistry;
import static java.util.Optional.ofNullable;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_APPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_CREATE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_INSERT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_PREPEND;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

/**
 * Class for validating {@link LiquibaseMutateInSpec} and transforming it to {@link MutateInSpec}
 */
public class MutateInSpecTransformer {

    private static final Set<MutateInType> multipleValueMutateInTypes = Sets.newHashSet(ARRAY_PREPEND, ARRAY_APPEND, ARRAY_CREATE,
            ARRAY_INSERT);

    private final MutateInValidatorRegistry validatorRegistry = Scope.getCurrentScope()
            .getSingleton(MutateInValidatorRegistry.class);


    public MutateInSpec toSpec(LiquibaseMutateInSpec liquibaseMutateInSpec) {
        // empty value can be used to replace or remove the whole document
        String path = ofNullable(liquibaseMutateInSpec.getPath()).orElse(StringUtils.EMPTY);
        Value value = liquibaseMutateInSpec.getValue();
        List<Value> values = liquibaseMutateInSpec.getValues();
        MutateInType mutateInType = liquibaseMutateInSpec.getMutateInType();

        MutateInValidator mutateInValidator = validatorRegistry.get(mutateInType);
        mutateInValidator.validate(path, value, values);

        if (multipleValueMutateInTypes.contains(mutateInType)) {
            return toMultipleValueSpec(path, value, values, mutateInType);
        }

        Object mappedValue = ofNullable(value)
                .map(Value::mapDataToType)
                .orElse(null);
        return mutateInType.toMutateInSpec(path, mappedValue);
    }


    private MutateInSpec toMultipleValueSpec(String path, Value value, List<Value> values, MutateInType mutateInType) {
        List<Object> valuesToSave = values.stream()
                .filter(valueToSave -> isNotEmpty(valueToSave.getData()))
                .map(Value::mapDataToType)
                .collect(Collectors.toList());

        if (valuesToSave.isEmpty()) {
            valuesToSave.add(value.mapDataToType());
        }
        return mutateInType.toMutateInSpec(path, valuesToSave);
    }

}
