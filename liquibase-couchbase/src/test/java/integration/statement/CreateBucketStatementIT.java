package integration.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import common.RandomizedScopeTestCase;
import common.matchers.CouchBaseClusterAssert;
import liquibase.ext.couchbase.exception.BucketExistsException;
import liquibase.ext.couchbase.statement.CreateBucketStatement;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

public class CreateBucketStatementIT extends RandomizedScopeTestCase {

    @BeforeEach
    void setUp() {
        if (clusterOperator.isBucketExists(CREATE_BUCKET_TEST_NAME)) {
            clusterOperator.dropBucket(CREATE_BUCKET_TEST_NAME);
        }
    }

    @Test
    public void Should_create_bucket_with_name() {
        if (clusterOperator.isBucketExists(CREATE_BUCKET_TEST_NAME)) {
            clusterOperator.dropBucket(CREATE_BUCKET_TEST_NAME);
        }
        BucketSettings settings = BucketSettings.create(CREATE_BUCKET_TEST_NAME);
        CreateBucketOptions bucketOptions = CreateBucketOptions.createBucketOptions();
        CreateBucketStatement createBucketStatement = new CreateBucketStatement(bucketOptions, settings, false);
        createBucketStatement.execute(clusterOperator);
        CouchBaseClusterAssert.assertThat(cluster).hasBucket(CREATE_BUCKET_TEST_NAME);
    }

    @Test
    public void Should_skip_if_bucket_exists_and_ignoring_enable() {
        clusterOperator.getOrCreateBucketOperator(CREATE_BUCKET_TEST_NAME);
        BucketSettings settings = BucketSettings.create(CREATE_BUCKET_TEST_NAME);
        CreateBucketOptions bucketOptions = CreateBucketOptions.createBucketOptions();
        CreateBucketStatement createBucketStatement = new CreateBucketStatement(bucketOptions, settings, true);

        assertThatNoException()
                .isThrownBy(() -> createBucketStatement.execute(clusterOperator));
    }

    @Test
    public void Should_throw_if_bucket_exists_and_ignoring_disable() {
        clusterOperator.getOrCreateBucketOperator(CREATE_BUCKET_TEST_NAME);
        BucketSettings settings = BucketSettings.create(CREATE_BUCKET_TEST_NAME);
        CreateBucketOptions bucketOptions = CreateBucketOptions.createBucketOptions();
        CreateBucketStatement createBucketStatement = new CreateBucketStatement(bucketOptions, settings, false);

        assertThatExceptionOfType(BucketExistsException.class)
                .isThrownBy(() -> createBucketStatement.execute(clusterOperator));
    }
}
