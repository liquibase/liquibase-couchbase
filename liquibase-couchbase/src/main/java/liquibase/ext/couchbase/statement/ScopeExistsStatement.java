package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 *
 * A statement that checks if a scope exists.
 *
 * @see liquibase.ext.couchbase.precondition.ScopeExistsPrecondition
 * @see CouchbaseStatement
 *
 */

@Data
@RequiredArgsConstructor
public class ScopeExistsStatement extends CouchbaseStatement {

    private final String bucketName;
    private final String scopeName;

    public boolean isScopeExists(CouchbaseConnection connection) {
        return Optional.of(connection.getCluster())
                .map(ClusterOperator::new)
                .filter(op -> op.isBucketExists(bucketName))
                .map(op -> op.getBucketOperator(bucketName))
                .map(op -> op.hasScope(scopeName))
                .orElse(false);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
