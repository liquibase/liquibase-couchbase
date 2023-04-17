package liquibase.ext.couchbase.statement;

import liquibase.Scope;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DropCollectionStatement extends CouchbaseStatement {
    private final Logger logger = Scope.getCurrentScope().getLog(DropCollectionStatement.class);

    private final Keyspace keyspace;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        String scopeName = keyspace.getScope();
        String collectionName = keyspace.getCollection();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());

        bucketOperator.dropCollection(collectionName, scopeName);
    }
}
