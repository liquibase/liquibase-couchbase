package integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.google.common.collect.ImmutableMap;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.InsertManyStatement;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class InsertManyStatementIT extends BucketTestCase {

    private final Map<String, String> testDocuments = ImmutableMap.of(
            TEST_ID, TEST_DOCUMENT,
            TEST_ID_2, TEST_DOCUMENT_2
    );

    @Test
    void Should_insert_many_documents() {
        InsertManyStatement statement =
                new InsertManyStatement(TEST_BUCKET, testDocuments, TEST_SCOPE, TEST_COLLECTION);

        statement.execute(database.getConnection());

        Bucket bucket = cluster.bucket(TEST_BUCKET);
        Collection collection = bucket.scope(TEST_SCOPE).collection(TEST_COLLECTION);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
    }

    @Test
    void Should_insert_many_documents_to_default_scope() {
        createCollectionInDefaultScope(TEST_COLLECTION_2);
        InsertManyStatement statement =
                new InsertManyStatement(TEST_BUCKET, testDocuments, DEFAULT_SCOPE, TEST_COLLECTION_2);

        statement.execute(database.getConnection());

        Collection collection = cluster.bucket(TEST_BUCKET).collection(TEST_COLLECTION_2);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_many_documents_to_default_scope_and_collection() {
        InsertManyStatement statement =
                new InsertManyStatement(TEST_BUCKET, testDocuments, DEFAULT_SCOPE, DEFAULT_COLLECTION);

        statement.execute(database.getConnection());

        Collection collection = cluster.bucket(TEST_BUCKET).defaultCollection();
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
    }


}
