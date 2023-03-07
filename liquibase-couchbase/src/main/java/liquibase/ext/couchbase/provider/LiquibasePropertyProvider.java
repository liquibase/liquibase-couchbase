package liquibase.ext.couchbase.provider;


import liquibase.ext.couchbase.exception.NoLiquibasePropertiesFileException;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Properties;

@UtilityClass
public class LiquibasePropertyProvider implements PropertyProvider {

    private static final Properties liquibaseProperties = readLiquibasePropertiesFile();
    private static final String ROOT_RESOURCE_FOLDER_PATH = "src";
    private static final String LIQUIBASE_PROPERTIES_FILE_NAME = "liquibase.properties";


    /**
     * Lookup first in the Env properties and then in the property file
     * @param name property name
     * @return non-null string value or default value
     */
    @NonNull
    public static String getPropertyOrDefault(String name, String defaultValue) {
        return PropertyProvider.getPropertyOrDefault(name, defaultValue, liquibaseProperties);
    }

    @SneakyThrows
    private static Properties readLiquibasePropertiesFile() {
        Properties properties = new Properties();
        try (FileReader fileReader = new FileReader(findLiquibaseFileFromRoot())) {
            properties.load(fileReader);
        }
        return properties;
    }


    private static File findLiquibaseFileFromRoot() {
        File root = new File(ROOT_RESOURCE_FOLDER_PATH);
        Collection<File> files = FileUtils.listFiles(root, null, true);
        for (File file : files) {
            if (file.getName().equals(LIQUIBASE_PROPERTIES_FILE_NAME)) {
                return file;
            }
        }
        throw new NoLiquibasePropertiesFileException();
    }

}
