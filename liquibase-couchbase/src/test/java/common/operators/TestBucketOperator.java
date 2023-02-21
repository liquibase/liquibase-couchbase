package common.operators;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.BucketOperator;

import java.util.Random;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;

public class TestBucketOperator extends BucketOperator {
    private final Random random;

    public TestBucketOperator(Bucket bucket) {
        super(bucket);
        random = new Random();
    }

    public TestBucketOperator(Cluster cluster) {
        super(getTestBucket(cluster));
        random = new Random();
    }

    public TestCollectionOperator getCollectionOperator(String collectionName, String scopeName) {
        return new TestCollectionOperator(
                bucket.scope(scopeName).collection(collectionName)
        );
    }

    public String createTestScope(String prefix) {
        String scopeName = prefix + "_" + random.nextInt(10000);
        bucket.collections().createScope(scopeName);
        return scopeName;
    }

    public String createTestScope() {
        String scopeName = TEST_SCOPE + "_" + random.nextInt(10000);
        bucket.collections().createScope(scopeName);
        return scopeName;
    }

    public String createTestCollection(String prefix, String scopeName) {
        String collectionName = prefix + "_" + random.nextInt(10000);
        bucket.collections().createCollection(create(collectionName, scopeName));
        return collectionName;
    }

    public String createTestCollection(String scopeName) {
        String collectionName = TEST_COLLECTION + "_" + random.nextInt(10000);
        bucket.collections().createCollection(create(collectionName, scopeName));
        return collectionName;
    }

    public void createDefaultTestCollection() {
        bucket.collections().createCollection(create(TEST_COLLECTION, TEST_SCOPE));
    }

    public void createDefaultTestScope() {
        bucket.collections().createScope(TEST_SCOPE);
    }

    public static Bucket getTestBucket(Cluster cluster) {
        return cluster.bucket(TEST_BUCKET);
    }
}
