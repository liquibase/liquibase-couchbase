package liquibase.ext.couchbase.validator;

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
class MutateInLongValueValidatorTest {

    private MutateInInsertUpsertUniqueValidator validator = new MutateInInsertUpsertUniqueValidator(MutateInType.INSERT);

    @Test
    void Should_validate_when_no_error() {
        String path = "field";
        List<Value> values = singletonList(new Value("newValue", DataType.STRING));

        assertDoesNotThrow(() -> validator.validate(path, values));
    }

    @Test
    void Should_throw_path_not_provided_exception_when_no_path() {
        List<Value> values = singletonList(new Value("newValue", DataType.STRING));

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
        List<Value> values = asList(new Value("newValue", DataType.STRING), new Value("newValue2", DataType.STRING));

        assertThrows(MutateInMultipleValuesNotAllowedException.class, () -> validator.validate(path, values));
    }

    @Test
    void Should_throw_no_value_exception_when_value_is_provided_but_empty() {
        String path = "field";
        List<Value> values = singletonList(new Value("", DataType.STRING));

        assertThrows(MutateInNoValueException.class, () -> validator.validate(path, values));
    }
}
