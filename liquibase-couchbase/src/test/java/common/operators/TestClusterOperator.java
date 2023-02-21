package common.operators;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import liquibase.ext.couchbase.operator.ClusterOperator;

import java.util.Random;

import static common.constants.TestConstants.INDEX;

public class TestClusterOperator extends ClusterOperator {
    private final Random random;

    public TestClusterOperator(Cluster cluster) {
        super(cluster);
        random = new Random();
    }

    public TestBucketOperator getBucketOperator(String bucketName) {
        requireBucketExists(bucketName);
        return new TestBucketOperator(cluster);
    }

    public TestBucketOperator getOrCreateBucketOperator(String bucketName) {
        if (!isBucketExists(bucketName)) {
            cluster.buckets().createBucket(BucketSettings.create(bucketName));
        }
        return new TestBucketOperator(cluster);
    }

    public String getTestIndexId() {
        return INDEX + "_" + random.nextInt(10000);
    }

}
