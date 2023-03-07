package liquibase.ext.couchbase.types;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * Types of data for mutate in operations
 */
@Getter
public enum DataType {
    LONG("Long"),
    DOUBLE("Double"),
    BOOLEAN("Boolean"),
    STRING("String"),
    JSON("Json"),
    JSON_ARRAY("JsonArray");

    private final String name;

    private static final Map<String, DataType> nameValueMap;

    static {
        nameValueMap = Arrays.stream(values())
            .collect(toMap(DataType::getName, Function.identity()));
    }

    DataType(String name) {
        this.name = name;
    }

    public static DataType getByName(String name) {
        return nameValueMap.get(name);
    }
}
