package common.operators;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.query.QueryOptions;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;

import java.util.concurrent.atomic.AtomicLong;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static com.couchbase.client.java.query.QueryScanConsistency.REQUEST_PLUS;
import static common.constants.TestConstants.INDEX;
import static java.lang.String.format;

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

    public void removeAllDocuments(Keyspace keyspace) {
        QueryOptions queryOptions = queryOptions().scanConsistency(REQUEST_PLUS);
        cluster.query(format("DELETE FROM %s", keyspace.getKeyspace()), queryOptions);
    }
}
