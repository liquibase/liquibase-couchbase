package integration.statement;

import com.couchbase.client.java.Collection;
import com.wdt.couchbase.Keyspace;

import org.junit.jupiter.api.Test;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import static com.wdt.couchbase.Keyspace.keyspace;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;

class InsertOneStatementIT extends BucketTestCase {

    @Test
    void Should_insert_one_document() {
        InsertOneStatement statement = new InsertOneStatement(TEST_KEYSPACE, document(TEST_ID,
                TEST_DOCUMENT.toString()));

        statement.execute(database.getConnection());

        Collection collection = getTestCollection();
        assertThat(collection).hasDocument(TEST_ID);
        removeDocFromTestCollection(TEST_ID);
    }

    @Test
    void Should_insert_document_to_default_scope() {
        createCollectionInDefaultScope(TEST_COLLECTION_2);
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, TEST_COLLECTION_2);
        InsertOneStatement statement = new InsertOneStatement(keyspace, document(TEST_ID,
                TEST_DOCUMENT.toString()));

        statement.execute(database.getConnection());

        Collection collection = getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).hasDocument(TEST_ID);
        removeDocFromDefaultScope(TEST_COLLECTION_2, TEST_ID);
        dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_document_to_default_scope_and_collection() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertOneStatement statement = new InsertOneStatement(keyspace, document(TEST_ID, TEST_DOCUMENT.toString()));

        statement.execute(database.getConnection());

        Collection collection = getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocument(TEST_ID);
        removeDocFromDefaultScope(DEFAULT_COLLECTION, TEST_ID);
    }

}
