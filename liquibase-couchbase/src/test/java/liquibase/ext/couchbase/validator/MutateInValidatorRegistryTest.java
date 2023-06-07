package liquibase.ext.couchbase.validator;

import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_APPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_CREATE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_INSERT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_INSERT_UNIQUE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.ARRAY_PREPEND;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.DECREMENT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.INCREMENT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.INSERT;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.REMOVE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.REPLACE;
import static liquibase.ext.couchbase.types.subdoc.MutateInType.UPSERT;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.LENIENT)
class MutateInValidatorRegistryTest {

    private MutateInValidatorRegistry validatorRegistry = new MutateInValidatorRegistry();

    @Test
    void Should_create_map_with_all_validators() {
        assertInstanceOf(MutateInRemoveValidator.class, validatorRegistry.get(REMOVE));
        assertInstanceOf(MutateInReplaceValidator.class, validatorRegistry.get(REPLACE));
        assertInstanceOf(MutateInLongValueValidator.class, validatorRegistry.get(INCREMENT));
        assertInstanceOf(MutateInLongValueValidator.class, validatorRegistry.get(DECREMENT));
        assertInstanceOf(MutateInArrayValidator.class, validatorRegistry.get(ARRAY_APPEND));
        assertInstanceOf(MutateInArrayValidator.class, validatorRegistry.get(ARRAY_CREATE));
        assertInstanceOf(MutateInArrayValidator.class, validatorRegistry.get(ARRAY_INSERT));
        assertInstanceOf(MutateInArrayValidator.class, validatorRegistry.get(ARRAY_PREPEND));
        assertInstanceOf(MutateInInsertUpsertUniqueValidator.class, validatorRegistry.get(INSERT));
        assertInstanceOf(MutateInInsertUpsertUniqueValidator.class, validatorRegistry.get(UPSERT));
        assertInstanceOf(MutateInInsertUpsertUniqueValidator.class, validatorRegistry.get(ARRAY_INSERT_UNIQUE));
    }

    @Test
    void Should_throw_exception_when_not_supported_type_provided() {
        assertThrows(IllegalArgumentException.class, () -> validatorRegistry.get(null));
    }
}
