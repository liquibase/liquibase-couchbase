package integration.statement;

import com.couchbase.client.java.Collection;
import com.google.common.collect.ImmutableList;
import liquibase.ext.couchbase.types.Keyspace;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

import common.BucketTestCase;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.InsertManyStatement;
import liquibase.ext.couchbase.types.Document;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class InsertManyStatementIT extends BucketTestCase {
    private BucketOperator bucketOperator;
    private CollectionOperator testCollectionOperator;
    private CollectionOperator defaultCollectionOperator;

    @BeforeEach
    void setUp() {
        bucketOperator = new BucketOperator(getBucket());
        testCollectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
        defaultCollectionOperator = bucketOperator.getCollectionOperator(DEFAULT_COLLECTION, DEFAULT_SCOPE);
    }

    private final List<Document> testDocuments = ImmutableList.of(
            new Document(TEST_ID, TEST_DOCUMENT.toString()),
            new Document(TEST_ID_2, TEST_DOCUMENT_2.toString())
    );

    @Test
    void Should_insert_many_documents() {
        InsertManyStatement statement = new InsertManyStatement(TEST_KEYSPACE, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        testCollectionOperator.removeDocs(TEST_ID, TEST_ID_2);
    }

    @Test
    void Should_insert_many_documents_to_default_scope() {
        bucketOperator.createCollectionInDefaultScope(TEST_COLLECTION_2);
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, TEST_COLLECTION_2);
        InsertManyStatement statement = new InsertManyStatement(keyspace, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        bucketOperator.dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_many_documents_to_default_scope_and_collection() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertManyStatement statement = new InsertManyStatement(keyspace, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocuments(TEST_ID, TEST_ID_2);
        defaultCollectionOperator.removeDocs(TEST_ID, TEST_ID_2);
    }
}
