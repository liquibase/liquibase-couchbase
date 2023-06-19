package liquibase.ext.couchbase.types;

import java.util.Arrays;

public enum GeneratorType {
    MONO_INCR,
    UUID;

    public static boolean isValidType(String type) {
        return Arrays.stream(GeneratorType.values()).anyMatch(x -> x.name().equals(type));
    }

}
