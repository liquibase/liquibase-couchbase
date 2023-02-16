package integration.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import common.BucketTestCase;
import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import liquibase.ext.couchbase.exception.CollectionNotExistsException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.DropCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration test for drop collection statement
 */
class DropCollectionStatementIT extends BucketTestCase {

    private TestClusterOperator clusterOperator = new TestClusterOperator(cluster);
    private final TestBucketOperator bucketOperator = clusterOperator.getBucketOperator(TEST_BUCKET);

    @Test
    void Collection_should_be_dropped_when_exists() {
        String collectionName = "dropCollectionName";
        bucketOperator.createCollection(collectionName, TEST_SCOPE);

        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, collectionName);
        DropCollectionStatement statement = new DropCollectionStatement(keyspace, false);

        statement.execute(clusterOperator);

        assertThat(bucketOperator.getBucket()).hasNoCollectionInScope(collectionName, TEST_SCOPE);
    }

    @Test
    void Should_throw_exception_if_bucket_not_exists() {
        String notCreatedBucket = "notCreatedBucket";
        Keyspace keyspace = keyspace(notCreatedBucket, TEST_SCOPE, "dropCollectionName");
        DropCollectionStatement statement = new DropCollectionStatement(keyspace, false);

        assertThatExceptionOfType(BucketNotFoundException.class)
                .isThrownBy(() -> statement.execute(new ClusterOperator(database.getConnection().getCluster())))
                .withMessage("Bucket [%s] not found.", notCreatedBucket);
    }

    @Test
    void Should_throw_exception_if_scope_not_exists() {
        String notCreatedScope = "notCreatedScope";
        String collectionName = "dropCollectionName";
        Keyspace keyspace = keyspace(TEST_BUCKET, notCreatedScope, collectionName);

        DropCollectionStatement statement = new DropCollectionStatement(keyspace, false);

        assertThatExceptionOfType(CollectionNotExistsException.class)
                .isThrownBy(() -> statement.execute(clusterOperator))
                .withMessage("Collection [%s] does not exist in scope [%s]", collectionName, notCreatedScope);
    }


}
