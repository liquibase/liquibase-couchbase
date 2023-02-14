package integration.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
import common.BucketTestCase;
import liquibase.ext.couchbase.exception.CollectionNotExistsException;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.DropCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.*;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration test for drop collection statement
 */
public class DropCollectionStatementIT extends BucketTestCase {

    private final Bucket bucket = cluster.bucket(TEST_BUCKET);
    private final BucketOperator bucketOperator = new BucketOperator(getBucket());

    @Test
    void Collection_should_be_dropped_when_exists() {
        String collectionName = "dropCollectionName";
        bucketOperator.createCollection(collectionName, TEST_SCOPE);

        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, collectionName);
        DropCollectionStatement statement = new DropCollectionStatement(keyspace);

        statement.execute(new ClusterOperator(database.getConnection().getCluster()));

        assertThat(bucket).hasNoCollectionInScope(collectionName, TEST_SCOPE);
    }

    @Test
    void Should_throw_exception_if_bucket_not_exists() {
        String notCreatedBucket = "notCreatedBucket";
        Keyspace keyspace = keyspace(notCreatedBucket, TEST_SCOPE, "dropCollectionName");
        DropCollectionStatement statement = new DropCollectionStatement(keyspace);

        assertThatExceptionOfType(BucketNotFoundException.class)
                .isThrownBy(() -> statement.execute(new ClusterOperator(database.getConnection().getCluster())))
                .withMessage("Bucket [%s] not found.", notCreatedBucket);
    }

    @Test
    void Should_throw_exception_if_scope_not_exists() {
        String notCreatedScope = "notCreatedScope";
        String collectionName = "dropCollectionName";
        Keyspace keyspace = keyspace(TEST_BUCKET, notCreatedScope, collectionName);
        ClusterOperator clusterOperator = new ClusterOperator(database.getConnection().getCluster());

        DropCollectionStatement statement = new DropCollectionStatement(keyspace);

        assertThatExceptionOfType(CollectionNotExistsException.class)
                .isThrownBy(() -> statement.execute(clusterOperator))
                .withMessage("Collection [%s] does not exist in scope [%s]", collectionName, notCreatedScope);
    }


}
