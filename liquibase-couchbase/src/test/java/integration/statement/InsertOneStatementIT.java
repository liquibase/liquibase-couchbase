package integration.statement;

import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.types.Keyspace;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import common.BucketTestCase;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
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

class InsertOneStatementIT extends BucketTestCase {
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

        statement.execute(database.getConnection());

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

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollectionFromDefaultScope(TEST_COLLECTION_2);
        assertThat(collection).hasDocument(TEST_ID);
        new CollectionOperator(collection).removeDoc(TEST_ID);
        bucketOperator.dropCollectionInDefaultScope(TEST_COLLECTION_2);
    }

    @Test
    void Should_insert_document_to_default_scope_and_collection() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        InsertOneStatement statement = new InsertOneStatement(keyspace, document(TEST_ID, TEST_DOCUMENT.toString()));

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION);
        assertThat(collection).hasDocument(TEST_ID);
        defaultCollectionOperator.removeDoc(TEST_ID);
    }

}
