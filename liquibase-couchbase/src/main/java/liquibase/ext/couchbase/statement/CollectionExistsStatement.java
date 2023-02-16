package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @see liquibase.ext.couchbase.precondition.CollectionExistsPrecondition
 * @see CouchbaseStatement
 */

@Data
@RequiredArgsConstructor
public class CollectionExistsStatement extends CouchbaseStatement {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;

    public boolean isCollectionExists(CouchbaseConnection connection) {
        ClusterOperator clusterOperator = new ClusterOperator(connection.getCluster());
        if (!clusterOperator.isBucketExists(bucketName)) {
            return false;
        }
        return clusterOperator.getBucketOperator(bucketName)
                .hasCollectionInScope(collectionName, scopeName);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
