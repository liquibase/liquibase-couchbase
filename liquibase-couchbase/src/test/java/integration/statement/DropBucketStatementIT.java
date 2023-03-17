package integration.statement;

import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.exception.BucketNotExistException;
import liquibase.ext.couchbase.statement.DropBucketStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class DropBucketStatementIT extends ConstantScopeTestCase {

    @BeforeEach
    void setUp() throws InterruptedException {
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
        if (!clusterOperator.isBucketExists(CREATE_BUCKET_TEST_NAME)) {
            clusterOperator.createBucket(CREATE_BUCKET_TEST_NAME);
        }
    }

    @Test
    void Should_drop_existing_bucket() {
        DropBucketStatement statement = new DropBucketStatement(CREATE_BUCKET_TEST_NAME, false);
        statement.execute(clusterOperator);

        assertThat(cluster).hasNoBucket(CREATE_BUCKET_TEST_NAME);
    }


    @Test
    void Should_throw_error_when_delete_non_existing_bucket() {
        String notFoundBucketName = "notFoundBucket";
        DropBucketStatement statement = new DropBucketStatement(notFoundBucketName, false);

        assertThatExceptionOfType(BucketNotExistException.class)
                .isThrownBy(() -> statement.execute(clusterOperator))
                .withMessage("Bucket [%s] not exists", notFoundBucketName);
    }

    @Test
    void Should_ignore_when_delete_non_existing_bucket() {
        String notFoundBucketName = "notFoundBucket";
        DropBucketStatement statement = new DropBucketStatement(notFoundBucketName, true);
        statement.execute(clusterOperator);
    }
}