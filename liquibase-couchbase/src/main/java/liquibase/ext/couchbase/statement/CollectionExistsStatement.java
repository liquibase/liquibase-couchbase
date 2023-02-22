package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * @see liquibase.ext.couchbase.precondition.CollectionExistsPrecondition
 * @see CouchbaseConditionalStatement
 */

@Data
@RequiredArgsConstructor
public class CollectionExistsStatement extends CouchbaseConditionalStatement {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;

    @Override
    public boolean isTrue(CouchbaseConnection connection) {
        return Optional.of(connection.getCluster())
            .map(ClusterOperator::new)
            .filter(op -> op.isBucketExists(bucketName))
            .map(op -> op.getBucketOperator(bucketName))
            .map(op -> op.hasCollectionInScope(collectionName, scopeName))
            .orElse(false);
    }

}
