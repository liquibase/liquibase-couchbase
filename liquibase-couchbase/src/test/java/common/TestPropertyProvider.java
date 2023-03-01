package common;

import liquibase.ext.couchbase.provider.PropertyProvider;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.util.Properties;

import static common.constants.TestConstants.PROPERTY_FILE_NAME;

public class TestPropertyProvider implements PropertyProvider {

    private static final Properties testProperties = readPropertiesFile();

    /**
     * Lookup first in the Env properties and then in the property file
     * @param name property name
     * @return non-null string value of the property
     *
     * @throws IllegalArgumentException if property isn't provided
     */
    @NotNull
    public static String getProperty(String name) {
        return PropertyProvider.getProperty(name, testProperties);
    }

    @SneakyThrows
    private static Properties readPropertiesFile() {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader(PROPERTY_FILE_NAME)) {
            properties.load(fileReader);
        }
        return properties;
    }

}
