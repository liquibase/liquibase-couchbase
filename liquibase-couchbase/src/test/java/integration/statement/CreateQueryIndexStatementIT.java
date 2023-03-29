package integration.statement;

import com.couchbase.client.java.manager.query.QueryIndex;
import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.CreateQueryIndexStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.matchers.CouchbaseClusterAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateQueryIndexStatementIT extends RandomizedScopeTestCase {
    private List<Field> fields;
    private Document testDocument;
    private final Keyspace keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);

    @BeforeEach
    void localSetUp() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        testDocument = collectionOperator.generateTestDoc();
        collectionOperator.insertDoc(testDocument);
        fields = testDocument.getFields();
    }

    @Test
    void Should_create_index_when_index_does_not_exist() {
        String indexToCreate = clusterOperator.getTestIndexId();
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
        clusterOperator.dropIndex(indexToCreate, bucketName);
    }

    @Test
    void Should_ignore_index_creation_with_the_same_name() {
        String indexToCreate = clusterOperator.getTestIndexId();
        clusterOperator.createCollectionQueryIndex(indexToCreate, keyspace, fields);
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(database.getConnection());

        List<QueryIndex> indexesForBucket = clusterOperator.getQueryIndexesForBucket(bucketName);
        assertEquals(1, indexesForBucket.size());
        // check that the index target column hasn't been overridden
        String indexTargetField = getIndexTargetField(indexesForBucket);
        assertEquals("`" + this.fields.get(0).getField() + "`", indexTargetField);
        clusterOperator.dropIndex(indexToCreate, bucketName);
    }

    @Nullable
    private static String getIndexTargetField(List<QueryIndex> indexesForBucket) {
        return indexesForBucket.stream().findFirst().map(QueryIndex::indexKey).map(x -> x.get(0)).map(
                String.class::cast).orElse(null);
    }

    @Test
    void Should_create_index_in_the_custom_namespace() {
        String indexToCreate = clusterOperator.getTestIndexId();
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        CreateQueryIndexStatement statement = statementForKeyspace(indexToCreate, keyspace);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
        clusterOperator.dropCollectionIndex(indexToCreate, keyspace);
    }

    @Test
    void Should_create_compound_index() {
        String indexToCreate = clusterOperator.getTestIndexId();
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
        return new CreateQueryIndexStatement(indexToCreate, keyspace, true, true, 0, fields);
    }

}
