package integration.statement;

import com.couchbase.client.core.deps.com.google.common.collect.ImmutableList;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.InsertDocumentsStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_COLLECTION_2;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class InsertDocumentsStatementIT extends TransactionStatementTest {
    private Keyspace keyspace;
    private List<Document> testDocuments;

    @BeforeEach
    void setUp() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);

        Document doc1 = collectionOperator.generateTestDoc();
        Document doc2 = collectionOperator.generateTestDoc();
        testDocuments = ImmutableList.of(doc1, doc2);
    }

    @Test
    void Should_insert_many_documents() {
        keyspace = keyspace(bucketName, scopeName, collectionName);
        InsertDocumentsStatement statement = new InsertDocumentsStatement(keyspace, testDocuments);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection).contains(testDocuments);
    }

    @Test
    void Should_insert_many_documents_within_default_scope() {
        bucketOperator.createCollectionInDefaultScope(TEST_COLLECTION_2);
        keyspace = keyspace(bucketName, DEFAULT_SCOPE, TEST_COLLECTION_2);
        InsertDocumentsStatement statement = new InsertDocumentsStatement(keyspace, testDocuments);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).contains(testDocuments);
    }

    @Test
    void Should_insert_many_documents_in_default_scope_and_collection() {
        keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertDocumentsStatement statement = new InsertDocumentsStatement(keyspace, testDocuments);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).contains(testDocuments);

    }

    @Test
    void Should_no_insert_documents_when_transaction_was_broken() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        InsertDocumentsStatement statement = new InsertDocumentsStatement(keyspace, testDocuments);

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> doInFailingTransaction(statement.asTransactionAction(clusterOperator)));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).doesNotContain(testDocuments);
    }
}
