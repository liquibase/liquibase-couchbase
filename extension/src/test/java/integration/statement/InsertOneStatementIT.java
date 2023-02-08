package integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.ExistsResult;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsertOneStatementIT extends BucketTestCase {

    @Test
    void Should_insert_one_document() {
        InsertOneStatement statement =
                new InsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, TEST_SCOPE, TEST_COLLECTION);
        statement.execute(database.getConnection());
        Bucket bucket = cluster.bucket(TEST_BUCKET);
        Collection collection = bucket.scope(TEST_SCOPE).collection(TEST_COLLECTION);
        ExistsResult result = collection.exists(TEST_ID);
        assertTrue(result.exists());
        collection.remove(TEST_ID);
    }

    @Test
    void Should_insert_document_to_default_scope() {
        createCollection(TEST_COLLECTION_2);
        InsertOneStatement statement =
                new InsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, null, TEST_COLLECTION_2);
        statement.execute(database.getConnection());
        Bucket bucket = cluster.bucket(TEST_BUCKET);
        Collection collection = bucket.collection(TEST_COLLECTION_2);
        ExistsResult result = collection.exists(TEST_ID);
        assertTrue(result.exists());
        collection.remove(TEST_ID);
        dropCollection(TEST_COLLECTION_2);
    }
    @Test
    void Should_insert_document_to_default_scope_and_collection() {
        InsertOneStatement statement =
                new InsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, null, null);
        statement.execute(database.getConnection());
        Bucket bucket = cluster.bucket(TEST_BUCKET);
        Collection collection = bucket.defaultCollection();
        ExistsResult result = collection.exists(TEST_ID);
        assertTrue(result.exists());
        collection.remove(TEST_ID);
    }

}
