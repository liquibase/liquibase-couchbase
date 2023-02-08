package common;

import lombok.extern.slf4j.Slf4j;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Singleton
 * Creates "testBucket","testScope" and "testCollection" before all tests
 */
@Slf4j
public class BucketTestCase extends CouchbaseContainerizedTest {

    static {
        createScope();
        createCollection();
    }

    protected static void createCollection() {
        createCollection(TEST_COLLECTION, TEST_SCOPE);
    }

    protected static void createCollection(String name, String scope) {
        cluster.bucket(TEST_BUCKET).collections()
                .createCollection(isBlank(scope) ? create(name) : create(name, TEST_SCOPE));
    }

    protected static void createCollection(String name) {
        cluster.bucket(TEST_BUCKET).collections().createCollection(create(name));
    }

    protected static void dropCollection(String name) {
        cluster.bucket(TEST_BUCKET).collections().dropCollection(create(name));
    }

    private static void createScope() {
        cluster.bucket(TEST_BUCKET).collections().createScope(TEST_SCOPE);
    }
}
