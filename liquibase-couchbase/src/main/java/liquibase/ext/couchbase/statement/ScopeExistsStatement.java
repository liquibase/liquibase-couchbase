package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

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
        Cluster cluster = connection.getCluster();
        try {
            cluster.buckets().getBucket(bucketName);
        } catch (BucketNotFoundException ex) {
            return false;
        }

        return cluster.bucket(bucketName).collections().getAllScopes().stream()
                .anyMatch(scopeSpec -> scopeSpec.name().equals(scopeName));
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
