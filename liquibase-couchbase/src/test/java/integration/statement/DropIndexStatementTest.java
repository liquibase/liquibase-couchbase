package integration.statement;

import common.BucketTestCase;
import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.DropIndexStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.FIELD_1;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.STANDARD_TIMEOUT;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT_3;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static java.util.Collections.singletonList;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;


class DropIndexStatementTest extends BucketTestCase {

    private Keyspace defaultKeyspace;
    private TestClusterOperator clusterOperator;
    private CollectionOperator testCollectionOperator;
    private CollectionOperator defaultCollectionOperator;

    @BeforeEach
    public void setUp() {
        clusterOperator = new TestClusterOperator(cluster);
        TestBucketOperator bucketOperator = clusterOperator.getBucketOperator(bucketName);
        scopeName = bucketOperator.createTestScope(TEST_SCOPE);
        collectionName = bucketOperator.createTestCollection(TEST_COLLECTION, scopeName);

        testCollectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        defaultCollectionOperator = bucketOperator.getCollectionOperator(DEFAULT_COLLECTION, DEFAULT_SCOPE);
        bucketOperator.getBucket().waitUntilReady(STANDARD_TIMEOUT);
        defaultKeyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
    }

    @Test
    void Should_drop_existing_index_in_default_scope() {
        defaultCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT_3);
        clusterOperator.createIndex(INDEX, bucketName, singletonList(FIELD_1));

        DropIndexStatement statement = new DropIndexStatement(INDEX, false, defaultKeyspace);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).doesNotHave(INDEX);
        defaultCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_drop_index_for_specific_keyspace() {
        testCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT_3);
        clusterOperator.createIndex(INDEX, TEST_KEYSPACE, singletonList(FIELD_1));

        DropIndexStatement statement = new DropIndexStatement(INDEX, false, TEST_KEYSPACE);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).doesNotHave(INDEX);
        testCollectionOperator.removeDoc(TEST_ID);
    }
}