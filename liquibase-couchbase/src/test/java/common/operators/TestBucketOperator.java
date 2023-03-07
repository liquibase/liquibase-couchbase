package common.operators;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.BucketOperator;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;

public class TestBucketOperator extends BucketOperator {

    private static long id = 0;

    public TestBucketOperator(Bucket bucket) {
        super(bucket);
    }

    public TestBucketOperator(Cluster cluster) {
        super(getTestBucket(cluster));
    }

    public TestCollectionOperator getCollectionOperator(String collectionName, String scopeName) {
        return new TestCollectionOperator(
                bucket.scope(scopeName).collection(collectionName)
        );
    }

    public String createTestScope(String prefix) {
        String scopeName = prefix + "_" + id++;
        bucket.collections().createScope(scopeName);
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
        return scopeName;
    }

    public String createTestScope() {
        String scopeName = TEST_SCOPE + "_" + id++;
        bucket.collections().createScope(scopeName);
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
        return scopeName;
    }

    public String createTestCollection(String prefix, String scopeName) {
        String collectionName = prefix + "_" + id++;
        bucket.collections().createCollection(create(collectionName, scopeName));
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
        return collectionName;
    }

    public String createTestCollection(String scopeName) {
        String collectionName = TEST_COLLECTION + "_" + id++;
        bucket.collections().createCollection(create(collectionName, scopeName));
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
        return collectionName;
    }

    public void createDefaultTestCollection() {
        bucket.collections().createCollection(create(TEST_COLLECTION, TEST_SCOPE));
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
    }

    public void createDefaultTestScope() {
        bucket.collections().createScope(TEST_SCOPE);
    }

    public static Bucket getTestBucket(Cluster cluster) {
        return cluster.bucket(TEST_BUCKET);
    }
}
