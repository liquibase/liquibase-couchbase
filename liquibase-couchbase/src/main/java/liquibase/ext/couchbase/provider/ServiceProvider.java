package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;

public interface ServiceProvider {

	String DEFAULT_SERVICE_SCOPE = "liquibaseServiceScope";
	String FALLBACK_SERVICE_BUCKET_NAME = "liquibaseServiceBucket";
	String CHANGE_LOG_COLLECTION = "DATABASECHANGELOG";

	Collection getServiceCollection(String collectionName);

	Scope getScopeOfCollection(String collectionName);

	String getServiceBucketName();

}
