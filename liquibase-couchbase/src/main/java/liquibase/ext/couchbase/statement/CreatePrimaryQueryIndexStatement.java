package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
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
    private final Keyspace keyspace;
    private final CreatePrimaryQueryIndexOptions options;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());
        Collection collection = bucketOperator.getCollection(keyspace.getCollection(), keyspace.getScope());
        clusterOperator.getCollectionOperator(collection).createPrimaryIndex(options);
    }
}
