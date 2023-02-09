package integration.statement;

import com.couchbase.client.java.Collection;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class InsertOneStatementIT extends BucketTestCase {

    @Test
    void Should_insert_one_document() {
        InsertOneStatement statement =
                new InsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, TEST_SCOPE, TEST_COLLECTION);

        statement.execute(database.getConnection());

        Collection collection = getCollection();
        assertThat(collection).hasDocument(TEST_ID);
        collection.remove(TEST_ID);
    }

    @Test
    void Should_insert_document_to_default_scope() {
        createCollectionInDefaultScope(TEST_COLLECTION_2);
        InsertOneStatement statement =
                new InsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, DEFAULT_SCOPE, TEST_COLLECTION_2);

        statement.execute(database.getConnection());

        Collection collection = getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).hasDocument(TEST_ID);
        dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_document_to_default_scope_and_collection() {
        InsertOneStatement statement =
                new InsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, DEFAULT_SCOPE, DEFAULT_COLLECTION);

        statement.execute(database.getConnection());

        Collection collection = getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocument(TEST_ID);
        collection.remove(TEST_ID);
    }

}
