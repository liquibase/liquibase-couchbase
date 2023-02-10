package integration.statement;

import com.couchbase.client.java.Collection;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.UpsertOneStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class UpsertOneStatementIT extends BucketTestCase {

    @Test
    void Should_insert_one_document() {
        UpsertOneStatement statement = new UpsertOneStatement(TEST_KEYSPACE, TEST_ID, TEST_DOCUMENT);

        statement.execute(database.getConnection());

        Collection collection = getTestCollection();
        assertThat(collection).hasDocument(TEST_ID);
        removeDocFromTestCollection(TEST_ID);
    }

    @Test
    void Should_update_document_if_exists() {
        insertDocInTestCollection(TEST_ID, TEST_DOCUMENT);
        UpsertOneStatement statement = new UpsertOneStatement(TEST_KEYSPACE, TEST_ID, TEST_DOCUMENT_2);

        statement.execute(database.getConnection());

        Collection collection = getTestCollection();
        assertThat(collection).extractingDocument(TEST_ID).itsContentEquals(TEST_DOCUMENT_2);
        removeDocFromTestCollection(TEST_ID);
    }

}
