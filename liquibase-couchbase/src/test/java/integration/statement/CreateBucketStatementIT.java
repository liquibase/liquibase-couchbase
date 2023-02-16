package integration.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import common.CouchbaseContainerizedTest;
import common.matchers.CouchBaseClusterAssert;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.CreateBucketStatement;
import org.junit.Test;

import static common.constants.TestConstants.CREATE_BUCKET_TEST_NAME;

public class CreateBucketStatementIT extends CouchbaseContainerizedTest {

    @Test
    public void Should_create_bucket_with_name() {
        BucketSettings settings = BucketSettings.create(CREATE_BUCKET_TEST_NAME);
        CreateBucketOptions bucketOptions = CreateBucketOptions.createBucketOptions();
        CreateBucketStatement createBucketStatement = new CreateBucketStatement(bucketOptions, settings);
        createBucketStatement.execute(new ClusterOperator(cluster));
        CouchBaseClusterAssert.assertThat(cluster).hasBucket(CREATE_BUCKET_TEST_NAME);
    }
}
