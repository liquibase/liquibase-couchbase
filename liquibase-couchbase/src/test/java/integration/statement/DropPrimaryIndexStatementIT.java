package integration.statement;

import com.couchbase.client.java.Collection;
import common.RandomizedScopeTestCase;

import java.util.UUID;

import liquibase.ext.couchbase.statement.DropPrimaryIndexStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.Test;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.matchers.CouchbaseClusterAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

class DropPrimaryIndexStatementIT extends RandomizedScopeTestCase {
    private Keyspace keyspace;

    @Test
    void Should_drop_Primary_index() {
        Collection collection = clusterOperator.getBucketOperator(bucketName)
                .getCollection(DEFAULT_COLLECTION, DEFAULT_SCOPE);
        clusterOperator.getCollectionOperator(collection).createPrimaryIndex();
        keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(keyspace, null);

        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).doesNotHavePrimary();
    }

    @Test
    void Should_drop_Primary_index_by_name() {
        String indexName = UUID.randomUUID().toString();
        Collection collection = clusterOperator.getBucketOperator(bucketName)
                .getCollection(DEFAULT_COLLECTION, DEFAULT_SCOPE);
        clusterOperator.getCollectionOperator(collection).createPrimaryIndex(createPrimaryQueryIndexOptions().indexName(indexName));
        keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(keyspace, indexName);

        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).hasNoPrimaryIndexForName(indexName);
    }

    @Test
    void Should_drop_primary_index_for_specific_keyspace() {
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
        keyspace = keyspace(bucketName, scopeName, collectionName);
        Collection collection = clusterOperator.getBucketOperator(bucketName)
                .getCollection(collectionName, scopeName);
        clusterOperator.getCollectionOperator(collection).createCollectionPrimaryIndex(null);

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(keyspace, null);
        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).doesNotHavePrimary();
    }
}