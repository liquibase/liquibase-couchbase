package common;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;

import java.util.Arrays;

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

    protected static void createCollectionInDefaultScope(String name) {
        getBucket().collections().createCollection(create(name));
    }

    protected static void dropCollectionInDefaultScope(String name) {
        getBucket().collections().dropCollection(create(name));
    }

    protected static Collection getCollection(String name, String scope) {
        return getBucket().scope(scope).collection(name);
    }

    protected static Collection getTestCollection() {
        return getCollection(TEST_COLLECTION, TEST_SCOPE);
    }

    protected static Collection getCollectionFromDefaultScope(String name) {
        return getBucket().collection(name);
    }

    protected static void insertDocInDefaultScope(String collection, String id, JsonObject content) {
        getCollectionFromDefaultScope(collection).insert(id, content);
    }

    protected static void insertDocInTestCollection(String id, JsonObject content) {
        getTestCollection().insert(id, content);
    }

    protected static void removeDocFromDefaultScope(String collection, String id) {
        getCollectionFromDefaultScope(collection).remove(id);
    }

    protected static void removeDocFromTestCollection(String id) {
        getTestCollection().remove(id);
    }

    protected static void removeDocsFromDefaultScope(String collection, String... ids) {
        Arrays.stream(ids).forEach(id -> removeDocFromDefaultScope(collection, id));
    }

    protected static void removeDocsFromTestCollection(String... ids) {
        Arrays.stream(ids).forEach(getTestCollection()::remove);
    }

}
