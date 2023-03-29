package common.operators;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.ClusterOperator;

import java.util.concurrent.atomic.AtomicLong;

import static common.constants.TestConstants.INDEX;

public class TestClusterOperator extends ClusterOperator {
    private static final AtomicLong id = new AtomicLong();

    public TestClusterOperator(Cluster cluster) {
        super(cluster);
    }

    public TestBucketOperator getBucketOperator(String bucketName) {
        requireBucketExists(bucketName);
        return new TestBucketOperator(cluster.bucket(bucketName));
    }

    public TestBucketOperator getOrCreateBucketOperator(String bucketName) {
        if (!isBucketExists(bucketName)) {
            createBucket(bucketName);
        }
        return this.getBucketOperator(bucketName);
    }

    public String getTestIndexId() {
        return INDEX + "_" + id.getAndIncrement();
    }
}
