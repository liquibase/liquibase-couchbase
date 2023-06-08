package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.json.JsonObject;
import com.google.common.collect.ImmutableMap;
import liquibase.ext.couchbase.exception.ProvideKeyFailedException;
import liquibase.ext.couchbase.provider.generator.IncrementalKeyGenerator;
import liquibase.ext.couchbase.provider.generator.UidKeyGenerator;
import liquibase.ext.couchbase.types.GeneratorType;
import liquibase.ext.couchbase.types.TokenType;
import lombok.Getter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static liquibase.ext.couchbase.types.GeneratorType.MONO_INCR;
import static liquibase.ext.couchbase.types.GeneratorType.UUID;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.replaceEach;
import static org.apache.commons.lang3.StringUtils.substringsBetween;

/**
 * Document key provider which uses expression to define complex key from fields/constants
 */
public class ExpressionDocumentKeyProvider implements DocumentKeyProvider {
    private static final String NO_FIELD_PATTERN = "Document doesn't contain field [%s]";
    private static final String NO_GENERATOR_PATTERN = "Unknown generator type [%s]";

    @Getter
    private final String expression;

    @Getter
    private final Map<String, TokenType> expTokens;
    private final Map<GeneratorType, Supplier<String>> generators =
            ImmutableMap.of(MONO_INCR, new IncrementalKeyGenerator()::generate,
                    UUID, new UidKeyGenerator()::generate);

    public ExpressionDocumentKeyProvider(String expression) {
        this.expression = expression;
        this.expTokens = parseExpression(expression);
    }

    private Map<String, TokenType> parseExpression(String expression) {
        if (isBlank(expression)) {
            throw new ProvideKeyFailedException("Document contains no key field");
        }
        Map<String, TokenType> tokens = new LinkedHashMap<>();
        for (Character separator : TokenType.getSeparators()) {
            String stringSeparator = separator.toString();
            String[] placeholders = substringsBetween(expression, stringSeparator, stringSeparator);
            TokenType type = TokenType.getBySeparator(separator);
            if (isNotEmpty(placeholders)) {
                Arrays.stream(placeholders).forEach(p -> tokens.put(p, type));
            }
        }

        return tokens;
    }

    @Override
    public String getKey(JsonObject jsonObject) {
        String[] tokens = new String[expTokens.size()];
        String[] replacements = new String[expTokens.size()];
        int i = 0;
        for (Map.Entry<String, TokenType> entry : expTokens.entrySet()) {
            TokenType type = entry.getValue();
            String token = entry.getKey();
            String tokenPlaceholder = type.getWrapped(token);
            tokens[i] = tokenPlaceholder;

            replacements[i] = processToken(jsonObject, type, token);
            i++;
        }
        return replaceEach(expression, tokens, replacements);
    }

    private String processToken(JsonObject jsonObject, TokenType type, String token) {
        if (TokenType.FIELD.equals(type)) {
            return Optional.of(token)
                    .map(jsonObject::getString)
                    .orElseThrow(() -> new ProvideKeyFailedException(String.format(NO_FIELD_PATTERN, token)));
        }
        if (TokenType.GENERATOR.equals(type)) {
            return Optional.of(token)
                    .filter(GeneratorType::isValidType)
                    .map(GeneratorType::valueOf)
                    .map(generators::get)
                    .map(Supplier::get)
                    .orElseThrow(() -> new ProvideKeyFailedException(String.format(NO_GENERATOR_PATTERN, token)));
        }
        return EMPTY;
    }
}
