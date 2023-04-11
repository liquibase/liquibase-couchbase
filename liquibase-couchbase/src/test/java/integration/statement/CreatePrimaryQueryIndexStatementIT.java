package integration.statement;

import com.couchbase.client.core.api.manager.CoreScopeAndCollection;
import com.couchbase.client.core.error.IndexExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.statement.CreatePrimaryQueryIndexStatement;
import liquibase.ext.couchbase.types.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.MANUALLY_CREATED_INDEX;
import static common.matchers.CouchbaseClusterAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatePrimaryQueryIndexStatementIT extends RandomizedScopeTestCase {

    @BeforeEach
    void localSetUp() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        Document document = collectionOperator.generateTestDoc();
        collectionOperator.insertDoc(document);
    }

    @AfterEach
    void cleanUp() {
        if (clusterOperator.indexExists(indexName, bucketName)) {
            clusterOperator.dropIndex(indexName, bucketName);
        }
        if (clusterOperator.indexExists(MANUALLY_CREATED_INDEX, bucketName)) {
            clusterOperator.dropIndex(MANUALLY_CREATED_INDEX, bucketName);
        }
    }

    @Test
    void Should_create_primary_index_when_primary_index_does_not_exist() {
        CreatePrimaryQueryIndexStatement statement =
                new CreatePrimaryQueryIndexStatement(bucketName, createOptions());
        statement.execute(clusterOperator);
        assertThat(cluster).queryIndexes(bucketName).hasPrimaryIndexForName(indexName);
    }

    @Test
    void Should_ignore_primary_index_creation_if_primary_index_exists() {
        createPrimaryIndexManually();
        CreatePrimaryQueryIndexStatement statement =
                new CreatePrimaryQueryIndexStatement(bucketName, createOptions());
        statement.execute(clusterOperator);
        String indexFromClusterName = clusterOperator.getQueryIndexes().getAllIndexes(bucketName).get(0).name();
        assertEquals(MANUALLY_CREATED_INDEX, indexFromClusterName);
    }

    @Test
    void Should_throw_an_exception_when_creating_second_primary_index_in_the_same_keyspace() {
        createPrimaryIndexManually();
        CreatePrimaryQueryIndexStatement statement = new CreatePrimaryQueryIndexStatement(bucketName,
                createOptions().indexName(MANUALLY_CREATED_INDEX).ignoreIfExists(false));

        assertThatExceptionOfType(IndexExistsException.class)
                .isThrownBy(() -> statement.execute(clusterOperator));
    }

    private CreatePrimaryQueryIndexOptions createOptions() {
        return CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .numReplicas(0)
                .indexName(indexName)
                .ignoreIfExists(true);
    }

    private void createPrimaryIndexManually() {
        CreatePrimaryQueryIndexOptions options = CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(MANUALLY_CREATED_INDEX);
        CoreScopeAndCollection scopeAndCollection = options.build().scopeAndCollection();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(bucketName);
        Collection collection = scopeAndCollection == null ?
                bucketOperator.getBucket().defaultCollection() :
                bucketOperator.getCollection(scopeAndCollection.collectionName(), scopeAndCollection.scopeName());
        clusterOperator.getCollectionOperator(collection).createPrimaryIndex(options);
    }
}
