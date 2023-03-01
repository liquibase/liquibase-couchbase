package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.UpsertOneStatement;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UpsertOneStatementIT extends TransactionStatementTest {
    private TestCollectionOperator collectionOperator;
    private Document testDocument;

    @BeforeEach
    public void setUp() {
        collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        testDocument = collectionOperator.generateTestDoc();
    }

    @Test
    void Should_insert_one_document() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        UpsertOneStatement statement = new UpsertOneStatement(keyspace, testDocument);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection).hasDocument(testDocument.getId());
    }

    @Test
    void Should_update_document_if_exists() {
        collectionOperator.insertDoc(testDocument);
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        UpsertOneStatement statement = new UpsertOneStatement(keyspace,
                document(testDocument.getId(), TEST_DOCUMENT_2.toString(), DataType.JSON));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection).extractingDocument(testDocument.getId()).itsContentEquals(TEST_DOCUMENT_2);
    }

    @Test
    void Should_no_insert_documents_when_transaction_was_broken() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        String uncreatedKey1 = "uncreated1";
        String uncreatedKey2 = "uncreated2";
        String uncreatedKey3 = "uncreated2";

        UpsertOneStatement statement1 = new UpsertOneStatement(keyspace,
                document(uncreatedKey1, testDocument.getValue()));
        UpsertOneStatement statement2 = new UpsertOneStatement(keyspace,
                document(uncreatedKey2, testDocument.getValue()));
        UpsertOneStatement statement3 = new UpsertOneStatement(keyspace,
                document(uncreatedKey3, testDocument.getValue()));

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> doInFailingTransaction(statement1.asTransactionAction(clusterOperator),
                statement2.asTransactionAction(clusterOperator), statement3.asTransactionAction(clusterOperator)));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection).hasNoDocument(uncreatedKey1).hasNoDocument(uncreatedKey2).hasNoDocument(uncreatedKey3);
    }

}
