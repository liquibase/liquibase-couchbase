package common.operators;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.operator.BucketOperator;

import java.util.concurrent.atomic.AtomicLong;

import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;

public class TestBucketOperator extends BucketOperator {

    private static final AtomicLong id = new AtomicLong();

    public TestBucketOperator(Bucket bucket) {
        super(bucket);
    }

    public TestBucketOperator(Cluster cluster) {
        super(getTestBucket(cluster));
    }

    public TestCollectionOperator getCollectionOperator(String collectionName, String scopeName) {
        return new TestCollectionOperator(getCollection(collectionName, scopeName));
    }

    public String createTestScope() {
        String scopeName = TEST_SCOPE + "_" + id.getAndIncrement();
        createScope(scopeName);
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
        return scopeName;
    }

    public String createTestCollection(String scopeName) {
        String collectionName = TEST_COLLECTION + "_" + id.getAndIncrement();
        createCollection(collectionName, scopeName);
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
        return collectionName;
    }

    public void createDefaultTestCollection() {
        createCollection(TEST_COLLECTION, TEST_SCOPE);
        bucket.waitUntilReady(CLUSTER_READY_TIMEOUT);
    }

    public void createDefaultTestScope() {
        createScope(TEST_SCOPE);
    }

    public static Bucket getTestBucket(Cluster cluster) {
        return cluster.bucket(TEST_BUCKET);
    }

    public Collection createOrGetCollection(String collectionName, String scopeName) {
        if (!hasCollectionInScope(collectionName, scopeName)) {
            createCollection(collectionName, scopeName);
        }
        return getCollection(collectionName, scopeName);
    }

}
