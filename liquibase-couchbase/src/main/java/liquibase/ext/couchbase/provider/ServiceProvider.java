package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;

import static liquibase.ext.couchbase.provider.LiquibasePropertyProvider.getPropertyOrDefault;

/**
 * An interface for providing context information for different services of the extension. Responsible for working with the service bucket
 * and its collections in the corresponding scopes.
 * @see ContextServiceProvider default implementation of this interface
 */

public interface ServiceProvider {

    String SERVICE_BUCKET_NAME = getPropertyOrDefault("serviceBucketName", "liquibaseServiceBucket");
    String DEFAULT_SERVICE_SCOPE = "liquibaseServiceScope";
    String CHANGE_LOG_COLLECTION = "DATABASECHANGELOG";

    Collection getServiceCollection(String collectionName);

    Scope getScopeOfCollection(String collectionName);

}
