package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.UpsertManyStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.constants.TestConstants.TEST_DOCUMENT_3;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UpsertManyStatementIT extends TransactionStatementTest {
    private TestCollectionOperator collectionOperator;
    private List<Document> testDocuments;

    @BeforeEach
    public void setUp() {
        collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        Document doc1 = collectionOperator.generateTestDoc();
        Document doc2 = collectionOperator.generateTestDoc();
        testDocuments = ImmutableList.of(doc1, doc2);
    }

    @Test
    void Should_insert_and_update_many_documents() {
        Document doc1 = collectionOperator.generateTestDoc();
        Document doc2 = collectionOperator.generateTestDoc();

        List<Document> testDocuments = Lists.newArrayList(doc1, doc2);
        collectionOperator.insertDoc(doc1.getId(), TEST_DOCUMENT_3);
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        UpsertManyStatement statement = new UpsertManyStatement(keyspace, testDocuments);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection).containDocuments(testDocuments);
    }

    @Test
    void Should_no_insert_documents_when_transaction_was_broken() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        UpsertManyStatement statement = new UpsertManyStatement(keyspace, testDocuments);

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> doInFailingTransaction(statement.asTransactionAction(clusterOperator)));

        Collection collection = bucketOperator.getCollection(collectionName,scopeName);
        assertThat(collection).hasNoDocuments(testDocuments);
    }

}
