package integration.statement;

import com.couchbase.client.java.Collection;

import org.junit.jupiter.api.Test;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.UpsertOneStatement;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_DOCUMENT_3;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_3;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class UpsertOneStatementIT extends BucketTestCase {

    @Test
    void Should_insert_one_document() {
        UpsertOneStatement statement =
                new UpsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT, TEST_SCOPE, TEST_COLLECTION);

        statement.execute(database.getConnection());

        Collection collection = getCollection();
        assertThat(collection).hasDocument(TEST_ID);
        collection.remove(TEST_ID);
    }

    @Test
    void Should_update_document_if_exists() {
        insertTestDocument(TEST_ID_3, TEST_DOCUMENT_3);
        UpsertOneStatement statement =
                new UpsertOneStatement(TEST_BUCKET, TEST_ID, TEST_DOCUMENT_2, TEST_SCOPE, TEST_COLLECTION);

        statement.execute(database.getConnection());

        Collection collection = getCollection();
        assertThat(collection).extractingDocument(TEST_ID).itsContentEquals(TEST_DOCUMENT_2);

        collection.remove(TEST_ID);
        collection.remove(TEST_ID_3);
    }

}
