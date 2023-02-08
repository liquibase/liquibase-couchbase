package integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.ExistsResult;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.InsertManyStatement;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InsertManyStatementIT extends BucketTestCase {

    public static final Map<String, String> TEST_DOCUMENTS = new HashMap<>();

    static {
        TEST_DOCUMENTS.put(TEST_ID, TEST_DOCUMENT);
        TEST_DOCUMENTS.put(TEST_ID_2, TEST_DOCUMENT_2);
    }

    @Test
    void Should_insert_many_documents() {
        InsertManyStatement statement =
                new InsertManyStatement(TEST_BUCKET, TEST_DOCUMENTS, TEST_SCOPE, TEST_COLLECTION);
        statement.execute(database.getConnection());

        Bucket bucket = cluster.bucket(TEST_BUCKET);

        Collection collection = bucket.scope(TEST_SCOPE).collection(TEST_COLLECTION);
        ExistsResult result1 = collection.exists(TEST_ID);
        ExistsResult result2 = collection.exists(TEST_ID_2);
        assertTrue(result1.exists());
        assertTrue(result2.exists());
    }

    @Test
    void Should_insert_many_documents_to_default_scope() {
        createCollection(TEST_COLLECTION_2);
        InsertManyStatement statement =
                new InsertManyStatement(TEST_BUCKET, TEST_DOCUMENTS, null, TEST_COLLECTION_2);
        statement.execute(database.getConnection());

        Bucket bucket = cluster.bucket(TEST_BUCKET);

        Collection collection = bucket.collection(TEST_COLLECTION_2);
        ExistsResult result1 = collection.exists(TEST_ID);
        ExistsResult result2 = collection.exists(TEST_ID_2);
        assertTrue(result1.exists());
        assertTrue(result2.exists());
        dropCollection(TEST_COLLECTION_2);
    }
    @Test
    void Should_insert_many_documents_to_default_scope_and_collection() {
        InsertManyStatement statement =
                new InsertManyStatement(TEST_BUCKET, TEST_DOCUMENTS, null, null);
        statement.execute(database.getConnection());

        Bucket bucket = cluster.bucket(TEST_BUCKET);

        Collection collection = bucket.defaultCollection();
        ExistsResult result1 = collection.exists(TEST_ID);
        ExistsResult result2 = collection.exists(TEST_ID_2);
        assertTrue(result1.exists());
        assertTrue(result2.exists());
    }


}
