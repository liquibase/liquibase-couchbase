package integration.statement;

import com.couchbase.client.java.Collection;

import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.UpsertOneStatement;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;

class UpsertOneStatementIT extends BucketTestCase {
    private BucketOperator bucketOperator;
    private CollectionOperator testCollectionOperator;

    @BeforeEach
    public void setUp() {
        bucketOperator = new BucketOperator(getBucket());
        testCollectionOperator = new CollectionOperator(bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
    }

    @Test
    void Should_insert_one_document() {
        UpsertOneStatement statement = new UpsertOneStatement(TEST_KEYSPACE, document(TEST_ID,
                TEST_DOCUMENT.toString()));

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).hasDocument(TEST_ID);
        testCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_update_document_if_exists() {
        testCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT);
        UpsertOneStatement statement = new UpsertOneStatement(TEST_KEYSPACE, document(TEST_ID,
                TEST_DOCUMENT_2.toString()));

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).extractingDocument(TEST_ID).itsContentEquals(TEST_DOCUMENT_2);
        testCollectionOperator.removeDoc(TEST_ID);
    }

}
