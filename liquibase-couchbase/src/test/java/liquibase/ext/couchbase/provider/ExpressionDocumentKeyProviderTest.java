package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.exception.ProvideKeyFailedException;
import liquibase.ext.couchbase.types.TokenType;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.when;

@MockitoSettings
class ExpressionDocumentKeyProviderTest {

    @Mock
    private JsonObject jsonObject;

    @Test
    void Should_parse_expression_correctly() {
        String expression = "#token#%value_value%%%%#%#%#";
        Map<String, TokenType> expected = new LinkedHashMap<>();
        expected.put("token", TokenType.GENERATOR);
        expected.put("value_value", TokenType.FIELD);
        expected.put("", TokenType.FIELD);
        expected.put("#", TokenType.FIELD);
        expected.put("%", TokenType.GENERATOR);
        ExpressionDocumentKeyProvider expressionDocumentKeyProvider = new ExpressionDocumentKeyProvider(expression);

        assertThat(expressionDocumentKeyProvider.getExpTokens()).isEqualTo(expected);
    }

    @Test
    void Should_throw_if_expression_is_null() {
        assertThatExceptionOfType(ProvideKeyFailedException.class)
                .isThrownBy(() -> new ExpressionDocumentKeyProvider(null))
                .withMessage("Can't provide key because: [Document contains no key field]");
    }

    @Test
    void Should_return_key_by_field() {
        ExpressionDocumentKeyProvider expressionDocumentKeyProvider = new ExpressionDocumentKeyProvider("%token%____");
        String expected = "expected";

        when(jsonObject.getString("token")).thenReturn(expected);

        assertThat(expressionDocumentKeyProvider.getKey(jsonObject)).isEqualTo(expected + "____");
    }

    @Test
    void Should_throw_exception_if_nothing_found_by_key() {
        ExpressionDocumentKeyProvider expressionDocumentKeyProvider = new ExpressionDocumentKeyProvider("%token%____");

        when(jsonObject.getString("token")).thenReturn(null);

        assertThatExceptionOfType(ProvideKeyFailedException.class)
                .isThrownBy(() -> expressionDocumentKeyProvider.getKey(jsonObject));
    }

    @Test
    void Should_return_generated_value_by_key() {
        ExpressionDocumentKeyProvider expressionDocumentKeyProvider = new ExpressionDocumentKeyProvider("#MONO_INCR#____");

        assertThat(expressionDocumentKeyProvider.getKey(jsonObject)).isEqualTo("0____");
        assertThat(expressionDocumentKeyProvider.getKey(jsonObject)).isEqualTo("1____");
        assertThat(expressionDocumentKeyProvider.getKey(jsonObject)).isEqualTo("2____");
    }

    @Test
    void Should_throw_exception_if_nothing_found_by_generator() {
        ExpressionDocumentKeyProvider expressionDocumentKeyProvider = new ExpressionDocumentKeyProvider("#UNEXISTING#");

        assertThatExceptionOfType(ProvideKeyFailedException.class)
                .isThrownBy(() -> expressionDocumentKeyProvider.getKey(jsonObject));
    }

}
