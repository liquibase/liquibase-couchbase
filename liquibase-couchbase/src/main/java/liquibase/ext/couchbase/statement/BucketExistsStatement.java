package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 *
 * @see liquibase.ext.couchbase.precondition.BucketExistsPrecondition
 * @see CouchbaseStatement
 *
 */

@Data
@RequiredArgsConstructor
public class BucketExistsStatement extends CouchbaseStatement {

    private final String bucketName;

    public boolean isBucketExists(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        try {
            cluster.buckets().getBucket(bucketName);
            return true;
        } catch (BucketNotFoundException ex) {
            return false;
        }
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
