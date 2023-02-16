package common.operators;

import com.couchbase.client.java.Bucket;
import liquibase.ext.couchbase.operator.BucketOperator;

import java.util.Random;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;

public class TestBucketOperator extends BucketOperator {
    private final Random random;

    public TestBucketOperator(Bucket bucket) {
        super(bucket);
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

    public String createTestCollection(String prefix, String scopeName) {
        String collectionName = prefix + "_" + random.nextInt(10000);
        bucket.collections().createCollection(create(collectionName, scopeName));
        return collectionName;
    }
}
