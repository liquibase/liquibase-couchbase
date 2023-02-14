package liquibase.ext.couchbase.statement;

import liquibase.Scope;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class DropCollectionStatement extends CouchbaseStatement {

    private static final String notExistsMsg = "Collection %s doesn't exists";
    private final Logger logger = Scope.getCurrentScope().getLog(DropCollectionStatement.class);

    private final Keyspace keyspace;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        String scope = keyspace.getScope();
        String collection = keyspace.getCollection();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());

        bucketOperator.requireScopeExists(collection, scope);
        bucketOperator.dropCollection(collection, scope);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        //TODO remove when all statements move to execute with cluster and we refactor NoSqlExecutor
        execute(new ClusterOperator(connection.getCluster()));
    }


}
