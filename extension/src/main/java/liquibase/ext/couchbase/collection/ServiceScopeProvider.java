package liquibase.ext.couchbase.collection;

import com.couchbase.client.java.Scope;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;

public interface ServiceScopeProvider {

	String DEFAULT_SERVICE_SCOPE = "liquibaseServiceScope";
	String FALLBACK_BUCKET_NAME = "liquibaseServiceBucket";

	Scope getServiceScope(CouchbaseLiquibaseDatabase database);

}
