package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.exception.ProvideKeyFailedException;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@MockitoSettings
class FieldDocumentKeyProviderTest {

    @Mock
    private JsonObject jsonObject;

    @Test
    void Should_get_json_key_value() {
        String keyField = "key";
        FieldDocumentKeyProvider fieldDocumentKeyProvider = new FieldDocumentKeyProvider(keyField);
        String expected = "expected";

        when(jsonObject.containsKey(keyField)).thenReturn(true);
        when(jsonObject.getString(keyField)).thenReturn(expected);

        assertThat(fieldDocumentKeyProvider.getKey(jsonObject)).isEqualTo(expected);
    }

    @Test
    void Should_throw_if_key_missed() {
        String keyField = "key";
        FieldDocumentKeyProvider fieldDocumentKeyProvider = new FieldDocumentKeyProvider(keyField);

        when(jsonObject.containsKey(keyField)).thenReturn(false);

        assertThatExceptionOfType(ProvideKeyFailedException.class)
                .isThrownBy(() -> fieldDocumentKeyProvider.getKey(jsonObject))
                .withMessage("Can't provide key because: [Document contains no key field]");
    }
}
