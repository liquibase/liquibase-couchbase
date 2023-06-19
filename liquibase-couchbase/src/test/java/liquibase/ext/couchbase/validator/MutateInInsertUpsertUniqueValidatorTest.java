package liquibase.ext.couchbase.validator;

import liquibase.ext.couchbase.exception.MutateInDataTypeNotAllowedException;
import liquibase.ext.couchbase.exception.MutateInMultipleValuesNotAllowedException;
import liquibase.ext.couchbase.exception.MutateInNoValueException;
import liquibase.ext.couchbase.exception.MutateInPathNotProvidedException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.LENIENT)
class MutateInInsertUpsertUniqueValidatorTest {

    private MutateInLongValueValidator validator = new MutateInLongValueValidator(MutateInType.INCREMENT);

    @Test
    void Should_validate_when_no_error() {
        String path = "field";
        List<Value> values = singletonList(new Value("1", DataType.LONG));

        assertDoesNotThrow(() -> validator.validate(path, values));
    }

    @Test
    void Should_throw_path_not_provided_exception_when_no_path() {
        List<Value> values = singletonList(new Value("1", DataType.LONG));

        assertThrows(MutateInPathNotProvidedException.class, () -> validator.validate(null, values));
    }

    @Test
    void Should_throw_no_value_exception_when_no_value() {
        String path = "field";
        List<Value> values = emptyList();

        assertThrows(MutateInNoValueException.class, () -> validator.validate(path, values));
    }

    @Test
    void Should_throw_multiple_value_exception_when_multiple_values() {
        String path = "field";
        List<Value> values = asList(new Value("1", DataType.LONG), new Value("1", DataType.LONG));

        assertThrows(MutateInMultipleValuesNotAllowedException.class, () -> validator.validate(path, values));
    }

    @Test
    void Should_throw_no_value_exception_when_value_is_provided_but_empty() {
        String path = "field";
        List<Value> values = singletonList(new Value("", DataType.LONG));

        assertThrows(MutateInNoValueException.class, () -> validator.validate(path, values));
    }

    @Test
    void Should_throw_type_not_allowed_exception_when_forbidden_type_provided() {
        String path = "field";
        List<Value> values = singletonList(new Value("someString", DataType.STRING));

        assertThrows(MutateInDataTypeNotAllowedException.class, () -> validator.validate(path, values));
    }
}
