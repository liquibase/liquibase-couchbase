package liquibase.ext.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Data
@RequiredArgsConstructor
public class DocumentExistsByKeyStatement extends CouchbaseStatement {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;
    private final String key;

    public boolean isCDocumentExists(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        try {
            cluster.buckets().getBucket(bucketName);
        } catch (BucketNotFoundException ex) {
            return false;
        }
        Optional<ScopeSpec> scope = cluster.bucket(bucketName).collections().getAllScopes().stream().filter(scopeSpec ->
                scopeSpec.name().equals(scopeName)).findFirst();
        if (!scope.isPresent()) {
            return false;
        }
        Optional<CollectionSpec> collection = scope.get().collections().stream()
                .filter(collectionSpec -> collectionSpec.name().equals(collectionName)).findFirst();
        if (!collection.isPresent()) {
            return false;
        }

        return isDocumentExists(cluster);
    }

    private boolean isDocumentExists(Cluster cluster) {
        try {
            cluster.bucket(bucketName).scope(scopeName).collection(collectionName).get(key);
            return true;
        } catch (DocumentNotFoundException ex) {
            return false;
        }
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        throw new UnsupportedOperationException();
    }
}
