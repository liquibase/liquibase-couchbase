package integration.statement;

import com.couchbase.client.core.error.BucketExistsException;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import common.RandomizedScopeTestCase;
import common.matchers.CouchbaseClusterAssert;
import liquibase.ext.couchbase.statement.CreateBucketStatement;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;

import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
        CreateBucketStatement createBucketStatement = new CreateBucketStatement(bucketOptions, settings);
        createBucketStatement.execute(clusterOperator);
        CouchbaseClusterAssert.assertThat(cluster).hasBucket(CREATE_BUCKET_TEST_NAME);
    }

    @Test
    public void Should_throw_if_bucket_exists() {
        clusterOperator.getOrCreateBucketOperator(CREATE_BUCKET_TEST_NAME);
        BucketSettings settings = BucketSettings.create(CREATE_BUCKET_TEST_NAME);
        CreateBucketOptions bucketOptions = CreateBucketOptions.createBucketOptions();
        CreateBucketStatement createBucketStatement = new CreateBucketStatement(bucketOptions, settings);

        assertThatExceptionOfType(BucketExistsException.class)
                .isThrownBy(() -> createBucketStatement.execute(clusterOperator));
    }
}
