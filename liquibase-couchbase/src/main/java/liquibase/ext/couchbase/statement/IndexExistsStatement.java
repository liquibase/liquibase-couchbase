package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @see liquibase.ext.couchbase.precondition.IndexExistsPrecondition
 * @see CouchbaseConditionalStatement
 */
@Data
@RequiredArgsConstructor
public class IndexExistsStatement extends CouchbaseConditionalStatement {

    private final String bucketName;
    private final String indexName;
    private final String scopeName;
    private final boolean isPrimary;

    public boolean isTrue(CouchbaseConnection connection) {
        ClusterOperator operator = new ClusterOperator(connection.getCluster());
        return operator.indexExists(indexName, bucketName, scopeName, isPrimary);
    }
}
