package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Cluster;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ClusterOperator {

    private final Cluster cluster;

    public BucketOperator getBucketOperator(String bucket) {
        return new BucketOperator(
                cluster.bucket(bucket)
        );
    }

}
