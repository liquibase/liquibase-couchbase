package org.liquibase.ext.couchbase.plugin.util;

import lombok.SneakyThrows;

import java.io.FileReader;
import java.util.Properties;

import static java.util.Optional.ofNullable;

public class TestPropertyProvider {
    private static final String PROPERTY_FILE_NAME = "src/test/resources/test.properties";
    private static final Properties testProperties = readPropertiesFile();

    @SneakyThrows
    private static Properties readPropertiesFile() {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader(PROPERTY_FILE_NAME)) {
            properties.load(fileReader);
        }
        return properties;
    }

    public static String getProperty(String name) {
        return ofNullable(testProperties.getProperty(name))
                .orElseThrow(() -> new IllegalArgumentException("No such registered property: " + name));
    }

}