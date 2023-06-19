package liquibase.ext.couchbase.provider;


import lombok.NonNull;

import java.util.Properties;

import static java.util.Optional.ofNullable;

public interface PropertyProvider {


    /**
     * Lookup first in the Env properties and then in the property file
     * @param name property name
     * @return non-null string value of property
     *
     * @throws IllegalArgumentException if property isn't provided
     */
    @NonNull
    static String getProperty(String name, Properties properties) {
        return ofNullable(findPropertyValue(name, properties))
                .orElseThrow(() -> new IllegalArgumentException("No such registered property: " + name));
    }

    /**
     * Lookup first in the Env properties and then in the property file
     * @param name property name
     * @return non-null string value or default value
     */
    static String findPropertyValue(@NonNull String name, @NonNull Properties properties) {
        return ofNullable(System.getenv(name))
                .orElse(properties.getProperty(name));
    }

}
