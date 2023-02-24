package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * A statement that checks if a document exists by key.
 * @see liquibase.ext.couchbase.precondition.DocumentExistsByKeyPrecondition
 * @see CouchbaseConditionalStatement
 */

@Data
@RequiredArgsConstructor
public class DocumentExistsByKeyStatement extends CouchbaseConditionalStatement {

    private final Keyspace keyspace;
    private final String key;

    @Override
    public boolean isTrue(CouchbaseConnection connection) {
        String bucket = keyspace.getBucket();
        String collectionName = keyspace.getCollection();
        String scopeName = keyspace.getScope();

        return Optional.ofNullable(connection.getCluster())
                .map(ClusterOperator::new)
                .filter(clusterOperator -> clusterOperator.isBucketExists(bucket))
                .map(clusterOperator -> clusterOperator.getBucketOperator(bucket))
                .filter(bucketOperator -> bucketOperator.hasCollectionInScope(collectionName, scopeName))
                .map(bucketOperator -> bucketOperator.getCollectionOperator(collectionName, scopeName))
                .map(collectionOperator -> collectionOperator.docExists(key))
                .orElse(false);
    }
}
