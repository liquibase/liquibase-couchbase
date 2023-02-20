package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.error.TransactionFailedException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.TransactionStatementTest;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.UpsertOneStatement;
import liquibase.ext.couchbase.types.Keyspace;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class UpsertOneStatementIT extends TransactionStatementTest {
    private BucketOperator bucketOperator;
    private CollectionOperator testCollectionOperator;

    @BeforeEach
    public void setUp() {
        bucketOperator = new BucketOperator(getBucket());
        testCollectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    }

    @Test
    void Should_insert_one_document() {
        UpsertOneStatement statement = new UpsertOneStatement(TEST_KEYSPACE, document(TEST_ID,
                TEST_DOCUMENT.toString()));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).hasDocument(TEST_ID);
        testCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_update_document_if_exists() {
        testCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT);
        UpsertOneStatement statement = new UpsertOneStatement(TEST_KEYSPACE, document(TEST_ID,
                TEST_DOCUMENT_2.toString()));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).extractingDocument(TEST_ID).itsContentEquals(TEST_DOCUMENT_2);
        testCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_no_insert_documents_when_transaction_was_broken() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        String uncreatedKey1 = "uncreated1";
        String uncreatedKey2 = "uncreated2";
        String uncreatedKey3 = "uncreated2";

        UpsertOneStatement statement1 = new UpsertOneStatement(keyspace, document(uncreatedKey1, TEST_DOCUMENT.toString()));
        UpsertOneStatement statement2 = new UpsertOneStatement(keyspace, document(uncreatedKey2, TEST_DOCUMENT.toString()));
        UpsertOneStatement statement3 = new UpsertOneStatement(keyspace, document(uncreatedKey3, TEST_DOCUMENT.toString()));

        assertThatExceptionOfType(TransactionFailedException.class)
                .isThrownBy(() -> {
                    doInFailingTransaction(statement1.asTransactionAction(clusterOperator),
                            statement2.asTransactionAction(clusterOperator),
                            statement3.asTransactionAction(clusterOperator)
                    );
                });

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection)
                .hasNoDocument(uncreatedKey1)
                .hasNoDocument(uncreatedKey2)
                .hasNoDocument(uncreatedKey3);
    }

}
