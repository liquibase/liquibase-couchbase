package common;

import com.couchbase.client.java.Bucket;
import lombok.extern.slf4j.Slf4j;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;

/**
 * Singleton
 * Creates "testBucket","testScope" and "testCollection" before all tests
 */
@Slf4j
public class BucketTestCase extends CouchbaseContainerizedTest {

    static {
        createTestScope();
        createTestCollection();
    }

    protected static Bucket getBucket() {
        return cluster.bucket(TEST_BUCKET);
    }

    protected static void createTestScope() {
        getBucket().collections().createScope(TEST_SCOPE);
    }

    protected static void dropTestScope() {
        cluster.bucket(TEST_BUCKET).collections().dropScope(TEST_SCOPE);
    }

    protected static void createTestCollection() {
        getBucket().collections().createCollection(create(TEST_COLLECTION, TEST_SCOPE));
    }
}
