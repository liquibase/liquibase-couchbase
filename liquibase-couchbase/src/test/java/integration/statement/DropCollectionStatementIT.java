package integration.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.exception.CollectionNotExistsException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.DropCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.Test;

import static common.matchers.CouchbaseBucketAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration test for drop collection statement
 */
class DropCollectionStatementIT extends RandomizedScopeTestCase {
    private static final String COLLECTION_TO_DROP = "dropCollectionName";

    @Test
    void Collection_should_be_dropped_when_exists() {
        bucketOperator.createCollection(COLLECTION_TO_DROP, scopeName);

        Keyspace keyspace = keyspace(bucketName, scopeName, COLLECTION_TO_DROP);
        DropCollectionStatement statement = new DropCollectionStatement(keyspace, false);

        statement.execute(clusterOperator);

        assertThat(bucketOperator.getBucket()).hasNoCollectionInScope(COLLECTION_TO_DROP, scopeName);
    }

    @Test
    void Should_throw_exception_if_bucket_not_exists() {
        String notCreatedBucket = "notCreatedBucket";
        Keyspace keyspace = keyspace(notCreatedBucket, scopeName, collectionName);
        DropCollectionStatement statement = new DropCollectionStatement(keyspace, false);

        assertThatExceptionOfType(BucketNotFoundException.class)
                .isThrownBy(() -> statement.execute(new ClusterOperator(clusterOperator.getCluster())))
                .withMessage("Bucket [%s] not found.", notCreatedBucket);
    }

    @Test
    void Should_throw_exception_if_scope_not_exists() {
        String notCreatedScope = "notCreatedScope";
        Keyspace keyspace = keyspace(bucketName, notCreatedScope, collectionName);

        DropCollectionStatement statement = new DropCollectionStatement(keyspace, false);

        assertThatExceptionOfType(CollectionNotExistsException.class)
                .isThrownBy(() -> statement.execute(clusterOperator))
                .withMessage("Collection [%s] does not exist in scope [%s]", collectionName, notCreatedScope);
    }


}
