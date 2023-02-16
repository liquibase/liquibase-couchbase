package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
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
        ClusterOperator operator = new ClusterOperator(connection.getCluster());
        return operator.isBucketExists(bucketName);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
