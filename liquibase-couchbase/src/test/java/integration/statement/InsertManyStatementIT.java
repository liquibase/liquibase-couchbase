package integration.statement;

import com.couchbase.client.java.Collection;
import com.google.common.collect.ImmutableList;
import liquibase.ext.couchbase.types.Keyspace;

import org.junit.jupiter.api.Test;

import java.util.List;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.InsertManyStatement;
import liquibase.ext.couchbase.types.Document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class InsertManyStatementIT extends BucketTestCase {

    private final List<Document> testDocuments = ImmutableList.of(
            new Document(TEST_ID, TEST_DOCUMENT.toString()),
            new Document(TEST_ID_2, TEST_DOCUMENT_2.toString())
    );

    @Test
    void Should_insert_many_documents() {
        InsertManyStatement statement = new InsertManyStatement(TEST_KEYSPACE, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = getTestCollection();
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        removeDocsFromTestCollection(TEST_ID, TEST_ID_2);
    }

    @Test
    void Should_insert_many_documents_to_default_scope() {
        createCollectionInDefaultScope(TEST_COLLECTION_2);
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, TEST_COLLECTION_2);
        InsertManyStatement statement = new InsertManyStatement(keyspace, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_many_documents_to_default_scope_and_collection() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertManyStatement statement = new InsertManyStatement(keyspace, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        removeDocsFromDefaultScope(DEFAULT_COLLECTION, TEST_ID, TEST_ID_2);
    }
}
