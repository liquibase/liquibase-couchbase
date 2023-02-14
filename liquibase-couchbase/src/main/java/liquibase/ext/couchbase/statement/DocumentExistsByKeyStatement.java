package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import java.util.Optional;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DocumentExistsByKeyStatement extends CouchbaseStatement {

    private final Keyspace keyspace;
    private final String key;

    public boolean isDocumentExists(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();

        return Optional.ofNullable(cluster)
                .map(Cluster::buckets)
                .filter(this::isBucketExist)
                .map(x -> tryGetScope(cluster))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::tryGetCollection)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(x -> isDocumentExists(cluster))
                .orElse(false);
    }

    private boolean isDocumentExists(Cluster cluster) {
        return cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection())
                .exists(key).exists();
    }

    private Optional<CollectionSpec> tryGetCollection(ScopeSpec scope) {
        return scope.collections().stream()
                .filter(collection -> collection.name().equals(keyspace.getCollection()))
                .findFirst();
    }

    private boolean isBucketExist(BucketManager buckets) {
        try {
            buckets.getBucket(keyspace.getBucket());
            return true;
        } catch (BucketNotFoundException ex) {
            return false;
        }
    }

    private Optional<ScopeSpec> tryGetScope(Cluster cluster) {
        return cluster.bucket(keyspace.getBucket())
                .collections()
                .getAllScopes().stream()
                .filter(sc -> sc.name().equals(keyspace.getScope()))
                .findFirst();
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
