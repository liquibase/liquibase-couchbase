package liquibase.ext.couchbase.statement;

import liquibase.Scope;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * A statement to create a collection
 * @see liquibase.ext.couchbase.change.CreateCollectionChange
 * @see liquibase.ext.couchbase.precondition.CollectionExistsPrecondition
 * @see CouchbaseStatement
 * @see Keyspace
 */

@Data
@RequiredArgsConstructor
public class CreateCollectionStatement extends CouchbaseStatement {

    private static final String existsMsg = "Collection %s already exists, skipping creation";
    private final Logger logger = Scope.getCurrentScope().getLog(CreateCollectionStatement.class);

    private final Keyspace keyspace;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());

        bucketOperator.createCollection(keyspace.getCollection(), keyspace.getScope());
    }
}
