package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.Scope;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * A statement to create secondary index for a keyspace
 * @see CouchbaseStatement
 * @see CreateQueryIndexOptions
 */

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CreateQueryIndexStatement extends CouchbaseStatement {
    private static final String existsMsg = "Index %s already exists, skipping creation";
    private final Logger logger = Scope.getCurrentScope().getLog(CreateQueryIndexStatement.class);
    private final String indexName;
    private final Keyspace keyspace;
    private final boolean deferred;
    private final int numReplicas;
    private final List<Field> fields;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        Collection collection = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollection(keyspace.getCollection(), keyspace.getScope());
        CreateQueryIndexOptions options = CreateQueryIndexOptions.createQueryIndexOptions()
                .deferred(deferred)
                .numReplicas(numReplicas);
        clusterOperator.getCollectionOperator(collection).createQueryIndex(indexName, fields, options);
    }
}