package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

import static com.couchbase.client.java.manager.bucket.BucketSettings.create;

@RequiredArgsConstructor
public class ContextServiceProvider implements ServiceProvider {

    private final CouchbaseLiquibaseDatabase database;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    @Override
    public Collection getServiceCollection(String collectionName) {
        CouchbaseConnection connection = database.getConnection();
        Bucket serviceBucket = Optional.ofNullable(connection.getDatabase())
            .orElseGet(() -> getServiceBucketFrom(connection.getCluster()));
        checkAndCreateScopeAndCollectionIn(serviceBucket, collectionName);
        return serviceBucket.scope(DEFAULT_SERVICE_SCOPE).collection(collectionName);
    }

    private void checkAndCreateScopeAndCollectionIn(Bucket bucket, String collectionName) {
        if(!serviceScopeExistsIn(bucket)) {
            bucket.collections().createScope(DEFAULT_SERVICE_SCOPE);
            bucket.waitUntilReady(TIMEOUT);
        }
        if(!collectionExistsIn(bucket, collectionName)) {
            bucket.collections().createCollection(CollectionSpec.create(collectionName, DEFAULT_SERVICE_SCOPE));
            bucket.waitUntilReady(TIMEOUT);
        }
    }

    private Bucket getServiceBucketFrom(Cluster cluster) {
        BucketManager manager = cluster.buckets();
        boolean serviceBucketExists = manager.getAllBuckets()
            .values()
            .stream()
            .anyMatch(bucketSettings -> bucketSettings.name().equals(FALLBACK_SERVICE_BUCKET_NAME));
        if(!serviceBucketExists) {
            manager.createBucket(create(FALLBACK_SERVICE_BUCKET_NAME));
            cluster.waitUntilReady(TIMEOUT);
        }
        return cluster.bucket(FALLBACK_SERVICE_BUCKET_NAME);
    }

    private boolean serviceScopeExistsIn(Bucket bucket) {
        return bucket.collections()
            .getAllScopes()
            .stream()
            .anyMatch(scope -> scope.name().equals(DEFAULT_SERVICE_SCOPE));
    }

    private boolean collectionExistsIn(Bucket bucket, String collectionName) {
        return bucket.collections()
            .getAllScopes()
            .stream()
            .map(ScopeSpec::collections)
            .flatMap(java.util.Collection::stream)
            .map(CollectionSpec::name)
            .anyMatch(collectionName::equals);
    }

    @Override
    public Scope getScopeOfCollection(String collectionName) {
        Collection serviceCollection = getServiceCollection(collectionName);
        return database.getConnection().getCluster().bucket(serviceCollection.bucketName()).scope(serviceCollection.scopeName());
    }

    @Override
    public String getServiceBucketName() {
        Bucket serviceBucket = database.getConnection().getDatabase();
        return Optional.ofNullable(serviceBucket)
                .map(Bucket::name)
                .orElse(FALLBACK_SERVICE_BUCKET_NAME);
    }

}
