package liquibase.ext.couchbase.validator;

import liquibase.ext.couchbase.exception.MutateInValueNotAllowedException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@MockitoSettings(strictness = Strictness.LENIENT)
class MutateInRemoveValidatorTest {

    private MutateInRemoveValidator validator = new MutateInRemoveValidator(MutateInType.REMOVE);

    @Test
    void Should_validate_when_no_error() {
        String path = "field";

        assertDoesNotThrow(() -> validator.validate(path, emptyList()));
    }

    @Test
    void Should_throw_no_value_allowed_exception_when_value_provided() {
        String path = "field";
        List<Value> values = singletonList(new Value("newValue", DataType.STRING));

        assertThrows(MutateInValueNotAllowedException.class, () -> validator.validate(path, values));
    }

}
