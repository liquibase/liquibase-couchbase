package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.QueryIndex;
import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.CreateQueryIndexStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.AfterEach;
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
    private final Keyspace keyspaceCustom = keyspace(bucketName, scopeName, collectionName);;

    private String indexToCreate = clusterOperator.getTestIndexId();


    @BeforeEach
    void localSetUp() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        testDocument = collectionOperator.generateTestDoc();
        collectionOperator.insertDoc(testDocument);
        fields = testDocument.getFields();
    }

    @AfterEach
    void cleanUp() {
        //TODO investigate does not work with - bucketOperator.getCollectionOperator(collectionName, scopeName).getCollection();
        Collection collectionDefault = clusterOperator.getBucketOperator(bucketName).getBucket().defaultCollection();
        Collection collectionCustom = cluster.bucket(keyspaceCustom.getBucket())
                .scope(keyspaceCustom.getScope())
                .collection(keyspaceCustom.getCollection());
        if (clusterOperator.getCollectionOperator(collectionCustom).collectionIndexExists(indexToCreate)) {
            clusterOperator.getCollectionOperator(collectionCustom).dropIndex(indexToCreate);
        }
        if (clusterOperator.indexExists(indexToCreate, bucketName)) {
            clusterOperator.getCollectionOperator(collectionDefault).dropIndex(indexToCreate);
        }

    }

    @Test
    void Should_create_index_when_index_does_not_exist() {
        indexToCreate = clusterOperator.getTestIndexId();
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
    }

    @Test
    void Should_ignore_index_creation_with_the_same_name() {
        indexToCreate = clusterOperator.getTestIndexId();
        Collection collection = cluster.bucket(keyspace.getBucket()).
                scope(keyspace.getScope()).collection(keyspace.getCollection());
        clusterOperator.getCollectionOperator(collection).createQueryIndex(indexToCreate, fields, null);
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(clusterOperator);

        List<QueryIndex> indexesForBucket = clusterOperator.getQueryIndexesForBucket(bucketName);
        assertEquals(1, indexesForBucket.size());
        // check that the index target column hasn't been overridden
        String indexTargetField = getIndexTargetField(indexesForBucket);
        assertEquals("`" + this.fields.get(0).getField() + "`", indexTargetField);
    }

    @Nullable
    private static String getIndexTargetField(List<QueryIndex> indexesForBucket) {
        return indexesForBucket.stream().findFirst().map(QueryIndex::indexKey).map(x -> x.get(0)).map(
                String.class::cast).orElse(null);
    }

    @Test
    void Should_create_index_in_the_custom_namespace() {
        indexToCreate = clusterOperator.getTestIndexId();
        CreateQueryIndexStatement statement = statementForKeyspace(indexToCreate, keyspaceCustom);

        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
    }

    @Test
    void Should_create_compound_index() {
        indexToCreate = clusterOperator.getTestIndexId();
        CreateQueryIndexStatement statement = statementForBucket(indexToCreate, bucketName);

        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).hasQueryIndexForName(indexToCreate);
    }

    private CreateQueryIndexStatement statementForBucket(String indexToCreate, String bucket) {
        Keyspace keyspace = keyspace(bucket, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        return statementForKeyspace(indexToCreate, keyspace);
    }

    private CreateQueryIndexStatement statementForKeyspace(String indexToCreate, Keyspace keyspace) {
        return new CreateQueryIndexStatement(indexToCreate, keyspace, true, true, 0, fields);
    }

}
