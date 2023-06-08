package liquibase.ext.couchbase.provider;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.GetAllQueryIndexesOptions;
import com.couchbase.client.java.manager.query.WatchQueryIndexesOptions;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import static com.couchbase.client.java.manager.bucket.BucketSettings.create;

/**
 * A concrete implementation of {@link ServiceProvider} interface. Uses either default bucket from {@link CouchbaseLiquibaseDatabase} or
 * creates a new one as a fallback option.<br><br>
 */

@RequiredArgsConstructor
public class ContextServiceProvider implements ServiceProvider {

    private final CouchbaseLiquibaseDatabase database;
    private static final Duration TIMEOUT = Duration.ofSeconds(10);
    private static final String INDEX_NAME = "primary";

    @Override
    public Collection getServiceCollection(String collectionName) {
        CouchbaseConnection connection = database.getConnection();
        Cluster cluster = connection.getCluster();
        Bucket serviceBucket = Optional.ofNullable(connection.getDatabase())
                .orElseGet(() -> getServiceBucketFrom(cluster));
        checkAndCreateScopeAndCollectionIn(serviceBucket, cluster, collectionName);
        return serviceBucket.scope(DEFAULT_SERVICE_SCOPE)
                .collection(collectionName);
    }

    private void checkAndCreateScopeAndCollectionIn(Bucket bucket, Cluster cluster, String collectionName) {
        BucketOperator bucketOperator = new BucketOperator(bucket);
        CollectionManager collections = bucket.collections();
        if (!bucketOperator.hasScope(DEFAULT_SERVICE_SCOPE)) {
            collections.createScope(DEFAULT_SERVICE_SCOPE);
            bucket.waitUntilReady(TIMEOUT);
        }
        if (!bucketOperator.hasCollectionInScope(collectionName, DEFAULT_SERVICE_SCOPE)) {
            collections.createCollection(CollectionSpec.create(collectionName, DEFAULT_SERVICE_SCOPE));
            bucket.waitUntilReady(TIMEOUT);
        }
        bucket.waitUntilReady(TIMEOUT);
        checkAndCreatePrimaryIndexIn(bucket, cluster, collectionName);
    }

    private Bucket getServiceBucketFrom(Cluster cluster) {
        ClusterOperator clusterOperator = new ClusterOperator(cluster);
        boolean serviceBucketExists = clusterOperator.isBucketExists(SERVICE_BUCKET_NAME);
        if (!serviceBucketExists) {
            BucketSettings bucketSettings = create(SERVICE_BUCKET_NAME);
            CreateBucketOptions bucketOptions = CreateBucketOptions.createBucketOptions();
            clusterOperator.createBucketWithOptionsAndSettings(bucketSettings, bucketOptions);
            cluster.waitUntilReady(TIMEOUT);
        }
        return cluster.bucket(SERVICE_BUCKET_NAME);
    }

    private void checkAndCreatePrimaryIndexIn(Bucket bucket, Cluster cluster, String collectionName) {
        GetAllQueryIndexesOptions getAllIndexesOptions = GetAllQueryIndexesOptions.getAllQueryIndexesOptions()
                .collectionName(collectionName)
                .scopeName(DEFAULT_SERVICE_SCOPE);
        boolean indexExists = cluster.queryIndexes()
                .getAllIndexes(bucket.name(), getAllIndexesOptions)
                .stream()
                .anyMatch(index -> Objects.equals(index.keyspace(), collectionName) && index.primary());
        if (indexExists) {
            return;
        }
        CreatePrimaryQueryIndexOptions createIndexOptions = CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions()
                .indexName(INDEX_NAME)
                .scopeName(DEFAULT_SERVICE_SCOPE)
                .collectionName(collectionName);
        cluster.queryIndexes()
                .createPrimaryIndex(bucket.name(), createIndexOptions);
        WatchQueryIndexesOptions watchOptions = WatchQueryIndexesOptions.watchQueryIndexesOptions()
                .watchPrimary(true)
                .scopeName(DEFAULT_SERVICE_SCOPE)
                .collectionName(collectionName);
        cluster.queryIndexes()
                .watchIndexes(bucket.name(), Collections.singletonList(INDEX_NAME), TIMEOUT, watchOptions);
    }

    @Override
    public Scope getScopeOfCollection(String collectionName) {
        Collection serviceCollection = getServiceCollection(collectionName);
        return database.getConnection()
                .getCluster()
                .bucket(serviceCollection.bucketName())
                .scope(serviceCollection.scopeName());
    }

}
