package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.TransactionStatementTest;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import liquibase.ext.couchbase.types.Keyspace;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class InsertOneStatementIT extends TransactionStatementTest {
    private BucketOperator bucketOperator;
    private CollectionOperator testCollectionOperator;
    private CollectionOperator defaultCollectionOperator;

    @BeforeEach
    public void setUp() {
        bucketOperator = new BucketOperator(getBucket());
        testCollectionOperator = new CollectionOperator(bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
        defaultCollectionOperator = new CollectionOperator(bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION));
    }

    @Test
    void Should_insert_one_document() {
        InsertOneStatement statement = new InsertOneStatement(TEST_KEYSPACE, document(TEST_ID,
                TEST_DOCUMENT.toString()));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).hasDocument(TEST_ID);
        testCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_insert_document_to_default_scope() {
        bucketOperator.createCollectionInDefaultScope(TEST_COLLECTION_2);
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, TEST_COLLECTION_2);
        InsertOneStatement statement = new InsertOneStatement(keyspace, document(TEST_ID,
                TEST_DOCUMENT.toString()));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).hasDocument(TEST_ID);
        new CollectionOperator(collection).removeDoc(TEST_ID);
        bucketOperator.dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_document_to_default_scope_and_collection() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertOneStatement statement = new InsertOneStatement(keyspace, document(TEST_ID, TEST_DOCUMENT.toString()));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocument(TEST_ID);
        defaultCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_no_insert_documents_when_transaction_was_broken() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        String existingKey = "createdDoc";
        String uncreatedKey1 = "uncreated1";
        String uncreatedKey2 = "uncreated2";
        defaultCollectionOperator.insertDoc(existingKey, TEST_DOCUMENT);

        InsertOneStatement statement1 = new InsertOneStatement(keyspace, document(uncreatedKey1, TEST_DOCUMENT.toString()));
        InsertOneStatement statement2 = new InsertOneStatement(keyspace, document(uncreatedKey2, TEST_DOCUMENT.toString()));
        InsertOneStatement statement3 = new InsertOneStatement(keyspace, document(existingKey, TEST_DOCUMENT.toString()));

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> doInTransaction(
                        statement1.asTransactionAction(clusterOperator),
                        statement2.asTransactionAction(clusterOperator),
                        statement3.asTransactionAction(clusterOperator)
                ));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection)
                .hasDocument(existingKey)
                .hasNoDocument(uncreatedKey1)
                .hasNoDocument(uncreatedKey2);

        defaultCollectionOperator.removeDoc(existingKey);
    }

}
