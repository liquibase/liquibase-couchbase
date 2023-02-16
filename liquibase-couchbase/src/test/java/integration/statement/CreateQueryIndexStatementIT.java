package integration.statement;

import com.couchbase.client.java.manager.query.QueryIndex;
import common.BucketTestCase;
import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.CreateQueryIndexStatement;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.FIELD_1;
import static common.constants.TestConstants.FIELD_2;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateQueryIndexStatementIT extends BucketTestCase {
    private static final List<Field> FIELDS = Arrays.asList(new Field(FIELD_1), new Field(FIELD_2));

    // TODO replace with test operator in every test class
    private TestCollectionOperator collectionOperator;
    private TestClusterOperator clusterOperator;
    private TestBucketOperator bucketOperator;

    @BeforeEach
    void localSetUp() {
        clusterOperator = new TestClusterOperator(cluster);
        bucketOperator = clusterOperator.getBucketOperator(bucketName);
        scopeName = bucketOperator.createTestScope(TEST_SCOPE);
        collectionName = bucketOperator.createTestCollection(TEST_COLLECTION, scopeName);

        collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        collectionOperator.insertTestDoc(TEST_DOCUMENT);
    }

    @AfterEach
    void cleanUp() {
        bucketOperator.dropCollection(collectionName, scopeName);
        bucketOperator.dropScope(scopeName);
    }

    @Test
    void Should_create_index_when_index_does_not_exist() {
        String indexToCreate = clusterOperator.getTestIndexId(INDEX);
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
        clusterOperator.dropIndex(indexToCreate, bucketName);
    }

    @Test
    void Should_ignore_index_creation_with_the_same_name() {
        String indexToCreate = clusterOperator.getTestIndexId(INDEX);
        clusterOperator.createIndex(indexToCreate, bucketName, Arrays.asList(FIELD_1, FIELD_2));
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(database.getConnection());

        List<QueryIndex> indexesForBucket = clusterOperator.getQueryIndexesForBucket(bucketName);
        assertEquals(1, indexesForBucket.size());
        //check that the index target column hasn't been overridden
        String indexTargetField = indexesForBucket.stream().findFirst()
                .map(QueryIndex::indexKey)
                .map(x -> x.get(0))
                .map(String.class::cast)
                .orElse(null);
        assertEquals("`" + FIELD_1 + "`", indexTargetField);
        clusterOperator.dropIndex(indexToCreate, bucketName);
    }

    @Test
    void Should_create_index_in_the_custom_namespace() {
        String indexToCreate = clusterOperator.getTestIndexId(INDEX);
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        CreateQueryIndexStatement statement = statementForKeyspace(indexToCreate, keyspace);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
        clusterOperator.dropIndex(indexToCreate, keyspace);
    }

    @Test
    void Should_create_compound_index() {
        String indexToCreate = clusterOperator.getTestIndexId(INDEX);
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
        clusterOperator.dropIndex(indexToCreate, bucketName);
    }

    private CreateQueryIndexStatement statementForBucket(String indexToCreate, String bucket) {
        Keyspace keyspace = keyspace(bucket, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        return statementForKeyspace(indexToCreate, keyspace);
    }

    private CreateQueryIndexStatement statementForKeyspace(String indexToCreate, Keyspace keyspace) {
        return new CreateQueryIndexStatement(indexToCreate, keyspace, true, true, 0, FIELDS);
    }
}
