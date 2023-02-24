package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @see liquibase.ext.couchbase.precondition.BucketExistsPrecondition
 * @see CouchbaseConditionalStatement
 */

@Data
@RequiredArgsConstructor
public class BucketExistsStatement extends CouchbaseConditionalStatement {

    private final String bucketName;

    public boolean isTrue(CouchbaseConnection connection) {
        ClusterOperator operator = new ClusterOperator(connection.getCluster());
        return operator.isBucketExists(bucketName);
    }
}
