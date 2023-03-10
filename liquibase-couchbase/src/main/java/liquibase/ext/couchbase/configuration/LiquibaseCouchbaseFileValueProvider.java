package liquibase.ext.couchbase.configuration;

import liquibase.Scope;
import liquibase.configuration.AbstractMapConfigurationValueProvider;
import liquibase.logging.Logger;
import liquibase.resource.Resource;
import liquibase.resource.ResourceAccessor;
import liquibase.servicelocator.LiquibaseService;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Custom properties provider for {@link CouchbaseLiquibaseConfiguration} Look up in classpath for file named
 * {@code liquibase-couchbase.properties}
 *
 * @see CouchbaseLiquibaseConfiguration
 */
@LiquibaseService
public class LiquibaseCouchbaseFileValueProvider extends AbstractMapConfigurationValueProvider {

    private final Logger log = Scope.getCurrentScope().getLog(getClass());
    private static final String propsFileName = "liquibase-couchbase.properties";

    private final Properties properties;

    public LiquibaseCouchbaseFileValueProvider() {
        properties = new Properties();
    }

    private void loadProps(ResourceAccessor resourceAccessor) throws IOException {
        Resource resource = resourceAccessor.getExisting(propsFileName);
        log.config("Loaded next properties from " + propsFileName + " " + properties.entrySet());
        InputStream inputStream = resource.openInputStream();
        properties.load(inputStream);
        inputStream.close();
    }

    @Override
    protected Map<?, ?> getMap() {
        properties.clear();
        ResourceAccessor resourceAccessor = Scope.getCurrentScope().getResourceAccessor();
        try {
            loadProps(resourceAccessor);
        } catch (IOException e) {
            log.config("No " + propsFileName + " file provided, using default properties");
        }
        return properties;
    }

    @Override
    protected String getSourceDescription() {
        return "Liquibase-couchbase.properties";
    }

    @Override
    public int getPrecedence() {
        return 100;
    }
}
