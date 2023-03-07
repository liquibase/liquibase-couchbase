package liquibase.ext.couchbase.configuration;

import liquibase.Scope;
import liquibase.configuration.AbstractMapConfigurationValueProvider;
import liquibase.logging.Logger;
import liquibase.servicelocator.LiquibaseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Paths.get;

/**
 * Custom properties provider for {@link CouchbaseLiquibaseConfiguration}
 * Look up in classpath for file named {@code liquibase-couchbase.properties}
 * @see CouchbaseLiquibaseConfiguration
 */
@LiquibaseService
public class LiquibaseCouchbaseFileValueProvider extends AbstractMapConfigurationValueProvider {

    private final Logger log = Scope.getCurrentScope().getLog(getClass());
    private static final String propsFileName = "liquibase-couchbase.properties";

    private final Properties properties;

    public LiquibaseCouchbaseFileValueProvider() {
        properties = new Properties();
        try (BufferedReader reader = newBufferedReader(get(getResource(propsFileName).getFile()))) {
            properties.load(reader);
            log.config("Loaded next properties from " + propsFileName + " " + properties.entrySet());
        } catch (IOException e) {
            log.config("No " + propsFileName + " file provided, using default properties");
        }
    }

    @Override
    protected Map<?, ?> getMap() {
        return properties;
    }

    @Override
    protected String getSourceDescription() {
        return "Liquibase-couchbase properties";
    }

    @Override
    public int getPrecedence() {
        return 100;
    }
}
