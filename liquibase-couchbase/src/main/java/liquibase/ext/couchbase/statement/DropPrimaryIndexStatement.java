package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isEmpty;


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
    private final String indexName;
    private final Keyspace keyspace;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        Collection collection = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getBucket()
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        if (isEmpty(indexName)) {
            clusterOperator.getCollectionOperator(collection).dropCollectionPrimaryIndex();
            return;
        }
        clusterOperator.getCollectionOperator(collection).dropIndex(indexName);
    }
}
