package liquibase.ext.couchbase.collection;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;

public class ContextServiceScopeProvider implements ServiceScopeProvider {

	@Override
	public Scope getServiceScope(CouchbaseLiquibaseDatabase database) {

		CouchbaseConnection connection = database.getConnection();
		Bucket defaultBucket = connection.getDatabase();
		if(defaultBucket != null) return defaultBucket.scope(DEFAULT_SERVICE_SCOPE);

		// Should be refactored to use BucketOperator from future merge
		Cluster cluster = connection.getCluster();
		BucketManager manager = cluster.buckets();
		boolean serviceBucketExists = manager.getAllBuckets()
			.values()
			.stream()
			.anyMatch(bucketSettings -> bucketSettings.name().equals(FALLBACK_BUCKET_NAME));
		if (!serviceBucketExists) {
			manager.createBucket(BucketSettings.create(FALLBACK_BUCKET_NAME));
		}
		return cluster.bucket(FALLBACK_BUCKET_NAME).scope(DEFAULT_SERVICE_SCOPE);
	}

}
