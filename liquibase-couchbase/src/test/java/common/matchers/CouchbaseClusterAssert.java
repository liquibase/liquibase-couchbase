package common.matchers;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CouchbaseClusterAssert extends AbstractAssert<CouchbaseClusterAssert, Cluster> {

    private CouchbaseClusterAssert(Cluster cluster) {
        super(cluster, CouchbaseClusterAssert.class);
    }

    public static CouchbaseClusterAssert assertThat(@NonNull Cluster actual) {
        return new CouchbaseClusterAssert(actual);
    }

    /**
     * Query indexes for default scope
     */
    public QueryIndexAssert queryIndexes(String bucketName) {
        QueryIndexManager queryIndexManager = actual.queryIndexes();
        List<QueryIndex> indexes = queryIndexManager.getAllIndexes(bucketName);
        return new QueryIndexAssert(queryIndexManager, indexes, bucketName);
    }

    /**
     * Query indexes for specific Keyspace {@link Keyspace}
     */
    public QueryIndexAssert queryIndexes(Keyspace keyspace) {
        QueryIndexManager queryIndexManager = actual.queryIndexes();
        List<QueryIndex> indexes = queryIndexManager.getAllIndexes(keyspace.getBucket());
        return new QueryIndexAssert(queryIndexManager, indexes, keyspace.getBucket());
    }

    public CouchbaseClusterAssert hasBucket(String bucketName) {
        BucketSettings bucket = actual.buckets().getBucket(bucketName);
        if (bucket == null) {
            failWithMessage("Bucket [%s] doesn't exist", bucketName);
        }
        return this;
    }

    public CouchbaseClusterAssert hasNoBucket(String bucketName) {
        Map<String, BucketSettings> allBuckets = actual.buckets().getAllBuckets();
        if (allBuckets.containsKey(bucketName)) {
            failWithMessage("Failed to delete bucket [%s]", bucketName);
        }
        return this;
    }

    public CouchbaseClusterAssert hasNoScope(String scopeName, String bucketName) {
        actual.waitUntilReady(Duration.ofSeconds(5L));
        Bucket bucket = actual.bucket(bucketName);
        if (bucket.collections().getAllScopes().stream().anyMatch(scopeSpec -> scopeSpec.name().equals(scopeName))) {
            failWithMessage("Failed to delete scope [%s]", scopeName);
        }
        return this;
    }

    public CouchbaseClusterAssert bucketUpdatedSuccessfully(String bucketName, BucketSettings settings) {
        BucketSettings actualSettings = actual.buckets().getBucket(bucketName);
        List<String> invalidFields = new ArrayList<>();
        if (settings.flushEnabled() != actualSettings.flushEnabled()) {
            invalidFields.add("flushEnabled");
        }
        if (settings.compressionMode() != actualSettings.compressionMode()) {
            invalidFields.add("compressionMode");
        }
        if (!settings.maxExpiry().equals(actualSettings.maxExpiry())) {
            invalidFields.add("maxExpiry");
        }
        if (settings.numReplicas() != actualSettings.numReplicas()) {
            invalidFields.add("numReplicas");
        }
        if (settings.ramQuotaMB() != actualSettings.ramQuotaMB()) {
            invalidFields.add("ramQuotaMB");
        }
        if (!invalidFields.isEmpty()) {
            failWithMessage("The <%s> properties of the bucket has not been updated", invalidFields);
        }

        return this;
    }
}
