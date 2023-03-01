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
     * Lookup firstly in Env properties and then in property file
     * @param name - Property name
     * @return not null value or
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
