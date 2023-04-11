package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.api.manager.CoreScopeAndCollection;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

/**
 * A statement to create primary index for a keyspace
 * @see CouchbaseStatement
 * @see CreatePrimaryQueryIndexOptions
 */

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CreatePrimaryQueryIndexStatement extends CouchbaseStatement {
    private final String bucketName;
    private final CreatePrimaryQueryIndexOptions options;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        CoreScopeAndCollection scopeAndCollection = options.build().scopeAndCollection();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(bucketName);
        Collection collection = scopeAndCollection == null ?
                bucketOperator.getBucket().defaultCollection() :
                bucketOperator.getCollection(scopeAndCollection.collectionName(), scopeAndCollection.scopeName());
        clusterOperator.getCollectionOperator(collection).createPrimaryIndex(options);
    }
}
