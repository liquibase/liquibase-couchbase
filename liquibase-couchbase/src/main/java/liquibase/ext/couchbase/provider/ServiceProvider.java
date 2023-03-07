package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;

/**
 * An interface for providing context information for different services of the extension. Responsible for working with the service bucket
 * and its collections in the corresponding scopes.
 * @see ContextServiceProvider default implementation of this interface
 */

public interface ServiceProvider {

    /**
     * Service bucket where all history is written and lock is acquired.
     */
    //todo let's remove constants from interface
    String SERVICE_BUCKET_NAME = CouchbaseLiquibaseConfiguration.SERVICE_BUCKET_NAME.getCurrentValue();
    String DEFAULT_SERVICE_SCOPE = "liquibaseServiceScope";
    String CHANGE_LOG_COLLECTION = "DATABASECHANGELOG";

    Collection getServiceCollection(String collectionName);

    Scope getScopeOfCollection(String collectionName);

}
