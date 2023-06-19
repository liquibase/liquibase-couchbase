package integration.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.statement.DropBucketStatement;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.constants.TestConstants.DROP_BUCKET_TEST_NAME;
import static common.matchers.CouchbaseClusterAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

class DropBucketStatementIT extends ConstantScopeTestCase {

    @BeforeAll
    static void setUp() {
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
        if (!clusterOperator.isBucketExists(DROP_BUCKET_TEST_NAME)) {
            clusterOperator.createBucket(DROP_BUCKET_TEST_NAME);
        }
    }

    @AfterAll
    static void cleanUp() {
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
        if (clusterOperator.isBucketExists(DROP_BUCKET_TEST_NAME)) {
            clusterOperator.dropBucket(DROP_BUCKET_TEST_NAME);
        }
    }

    @Test
    void Should_drop_existing_bucket() {
        DropBucketStatement statement = new DropBucketStatement(DROP_BUCKET_TEST_NAME);
        statement.execute(clusterOperator);

        assertThat(cluster).hasNoBucket(DROP_BUCKET_TEST_NAME);
    }


    @Test
    void Should_throw_error_when_delete_non_existing_bucket() {
        String notFoundBucketName = "notFoundBucket1";
        DropBucketStatement statement = new DropBucketStatement(notFoundBucketName);

        assertThatExceptionOfType(BucketNotFoundException.class)
                .isThrownBy(() -> statement.execute(clusterOperator))
                .withMessage("Bucket [%s] not found.", notFoundBucketName);
    }

    @Test
    @Disabled("Not actual, flag deleted")
    void Should_ignore_when_delete_non_existing_bucket() {
        String notFoundBucketName = "notFoundBucket2";
        DropBucketStatement statement = new DropBucketStatement(notFoundBucketName);
        assertThatNoException().isThrownBy(() -> statement.execute(clusterOperator));
    }
}