package liquibase.integration;

import lombok.extern.slf4j.Slf4j;
import static com.couchbase.client.java.manager.collection.CollectionSpec.create;

/**
 * Creates and deletes "testBucket" before and after test
 */
@Slf4j
public class BucketTestCase extends CouchbaseContainerizedTest {

    static {
        createScope();
        createCollection();
    }

    private static void createCollection() {
        cluster.bucket(TEST_BUCKET).collections().createCollection(create(TEST_COLLECTION, TEST_SCOPE));
    }

    private static void createScope() {
        cluster.bucket(TEST_BUCKET).collections().createScope(TEST_SCOPE);
    }
}
