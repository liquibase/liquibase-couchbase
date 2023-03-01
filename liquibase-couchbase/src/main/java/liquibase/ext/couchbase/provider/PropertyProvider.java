package liquibase.ext.couchbase.provider;


import lombok.NonNull;

import java.util.Properties;

import static java.util.Optional.ofNullable;

public interface PropertyProvider {


    /**
     * Lookup firstly in Env properties and then in property file
     * @param name - Property name
     * @return not null value or
     *
     * @throws IllegalArgumentException if property isn't provided
     */
    @NonNull
    static String getProperty(String name, Properties properties) {
        return ofNullable(findPropertyValue(name, properties))
                .orElseThrow(() -> new IllegalArgumentException("No such registered property: " + name));
    }

    /**
     * Lookup firstly in Env properties and then in property file
     * @param name - Property name
     * @return not null value or default value
     */
    @NonNull
    static String getPropertyOrDefault(String name, String defaultValue, Properties properties) {
        return ofNullable(findPropertyValue(name, properties))
                .orElse(defaultValue);
    }

    static String findPropertyValue(@NonNull String name, @NonNull Properties properties) {
        return ofNullable(System.getenv(name))
                .orElse(properties.getProperty(name));
    }

}
