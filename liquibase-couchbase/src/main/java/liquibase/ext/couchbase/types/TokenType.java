package liquibase.ext.couchbase.types;

import liquibase.ext.couchbase.exception.KeyExpressionParseFailedException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.wrap;

public enum TokenType {
    FIELD('%'),
    GENERATOR('#');
    @Getter
    private final char separator;
    @Getter
    private static final List<Character> separators = Arrays.stream(TokenType.values())
            .map(TokenType::getSeparator).collect(toList());

    TokenType(char separator) {
        this.separator = separator;
    }

    public static TokenType getBySeparator(char separator) {
        return Arrays.stream(TokenType.values())
                .filter(x -> x.separator == separator)
                .findFirst()
                .orElseThrow(() ->
                        new KeyExpressionParseFailedException(String.format("Unknown type of placeholder %s", separator)));
    }

    public String getWrapped(String tokenValue) {
        return wrap(tokenValue, this.separator);
    }
}
