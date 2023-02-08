package common;

import com.couchbase.client.java.Collection;
import lombok.extern.slf4j.Slf4j;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_ID;
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

    protected static void createCollectionInDefaultScope(String name) {
        cluster.bucket(TEST_BUCKET).collections().createCollection(create(name));
    }

    protected static void dropCollectionInDefaultScope(String name) {
        cluster.bucket(TEST_BUCKET).collections().dropCollection(create(name));
    }

    protected static Collection getCollection() {
        return cluster.bucket(TEST_BUCKET).scope(TEST_SCOPE).collection(TEST_COLLECTION);
    }

    protected static Collection getCollectionFromDefaultScope(String name) {
        return cluster.bucket(TEST_BUCKET).collection(name);
    }

    protected static void insertTestDocument(String content) {
        getCollection().insert(TEST_ID, content);
    }

    private static void createScope() {
        cluster.bucket(TEST_BUCKET).collections().createScope(TEST_SCOPE);
    }
}
