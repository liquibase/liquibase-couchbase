package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Data
@RequiredArgsConstructor
public class CollectionExistsStatement extends CouchbaseStatement {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;

    public boolean isCollectionExists(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        if (!isBucketExists(cluster)) {
            return false;
        }
        Optional<ScopeSpec> scope = findScope(cluster);
        return scope.map(scopeSpec -> scopeSpec.collections().stream()
                .anyMatch(collectionSpec -> collectionSpec.name().equals(collectionName)))
                .orElse(false);
    }

    private boolean isBucketExists(Cluster cluster) {
        try {
            cluster.buckets().getBucket(bucketName);
            return true;
        } catch (BucketNotFoundException ex) {
            return false;
        }
    }

    private Optional<ScopeSpec> findScope(Cluster cluster) {
        return cluster.bucket(bucketName).collections().getAllScopes().stream().filter(scopeSpec ->
                scopeSpec.name().equals(scopeName)).findFirst();
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
