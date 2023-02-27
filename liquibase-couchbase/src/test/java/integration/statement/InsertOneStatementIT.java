package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class InsertOneStatementIT extends TransactionStatementTest {
    private TestCollectionOperator collectionOperator;

    @BeforeEach
    public void setUp() {
        collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
    }

    @Test
    void Should_insert_one_document() {
        Document doc = collectionOperator.generateTestDoc();
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        InsertOneStatement statement = new InsertOneStatement(keyspace, doc);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection).hasDocument(doc.getId());
    }

    @Test
    void Should_insert_document_to_default_scope() {
        Document doc = collectionOperator.generateTestDoc();
        collectionName = bucketOperator.createTestCollection(DEFAULT_SCOPE);
        Keyspace keyspace = keyspace(bucketName, DEFAULT_SCOPE, collectionName);
        InsertOneStatement statement = new InsertOneStatement(keyspace, doc);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(collectionName);
        assertThat(collection).hasDocument(doc.getId());
    }

    @Test
    void Should_insert_document_to_default_scope_and_collection() {
        Document doc = collectionOperator.generateTestDoc();
        Keyspace keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertOneStatement statement = new InsertOneStatement(keyspace, doc);

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocument(doc.getId());
    }

    @Test
    void Should_no_insert_documents_when_transaction_was_broken() {
        Document doc = collectionOperator.generateTestDoc();
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        String existingKey = doc.getId();
        String uncreatedKey1 = "uncreated1";
        String uncreatedKey2 = "uncreated2";
        collectionOperator.insertDoc(doc);

        InsertOneStatement statement1 = new InsertOneStatement(keyspace, document(uncreatedKey1, doc.getContent()));
        InsertOneStatement statement2 = new InsertOneStatement(keyspace, document(uncreatedKey2, doc.getContent()));
        InsertOneStatement statement3 = new InsertOneStatement(keyspace, document(existingKey, doc.getContent()));

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> doInTransaction(
                        statement1.asTransactionAction(clusterOperator),
                        statement2.asTransactionAction(clusterOperator),
                        statement3.asTransactionAction(clusterOperator)
                ));

        Collection collection = bucketOperator.getCollection(collectionName, scopeName);
        assertThat(collection)
                .hasDocument(existingKey)
                .hasNoDocument(uncreatedKey1)
                .hasNoDocument(uncreatedKey2);

        collectionOperator.removeDoc(existingKey);
    }

}
