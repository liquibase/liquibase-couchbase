package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * A statement to drop primary index for a keyspace
 * @see CouchbaseStatement
 * @see ClusterOperator
 * @see Keyspace
 */

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropPrimaryIndexStatement extends CouchbaseStatement {
    private final Keyspace keyspace;

    private final String indexName;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        CollectionOperator collectionOperator = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope());
        if (indexName != null) {
            collectionOperator.dropIndex(indexName);
            return;
        }
        collectionOperator.dropCollectionPrimaryIndex();
    }
}
