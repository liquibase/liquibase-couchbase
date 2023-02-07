package common;

import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.util.Properties;

import lombok.SneakyThrows;
import static common.constants.TestConstants.PROPERTY_FILE_NAME;

public class PropertyProvider {


    private static final Properties fileProperties = readPropertiesFile();


    /**
     * Lookup firstly in Env properties and then in property file
     *
     * @param name - Property name
     * @return not null value or
     * @throws IllegalArgumentException if property isn't provided
     */
    @NotNull
    public static String getProperty(String name) {
        String environmentValue = System.getProperty(name);
        if (environmentValue != null) {
            return environmentValue;
        }

        String property = fileProperties.getProperty(name);
        if (property == null) {
            throw new IllegalArgumentException("No such registered property: " + name);
        }

        return property;
    }

    @SneakyThrows
    private static Properties readPropertiesFile() {
        Properties p = new Properties();
        p.load(new FileReader(PROPERTY_FILE_NAME));
        return p;
    }

}
