package common.operators;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.ClusterOperator;

import java.util.List;
import java.util.Random;

public class TestClusterOperator extends ClusterOperator {
    private final Random random;

    public TestClusterOperator(Cluster cluster) {
        super(cluster);
        random = new Random();
    }

    public TestBucketOperator getBucketOperator(String bucketName) {
        requireBucketExists(bucketName);
        return new TestBucketOperator(
                cluster.bucket(bucketName)
        );
    }

    public String createTestIndex(String prefix, String bucketName, List<String> fields) {
        String idxName = getTestIndexId(prefix);
        createIndex(idxName, bucketName, fields);
        return idxName;
    }

    public String getTestIndexId(String prefix) {
        return prefix + "_" + random.nextInt(10000);
    }
}
