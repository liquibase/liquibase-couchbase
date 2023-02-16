package liquibase.ext.couchbase.statement;

import liquibase.Scope;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.CollectionNotExistsException;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@Data
@RequiredArgsConstructor
public class DropCollectionStatement extends CouchbaseStatement {
    private static final String skipMsg = "Collection %s already absent, skipping removing";
    private final Logger logger = Scope.getCurrentScope().getLog(DropCollectionStatement.class);

    private final Keyspace keyspace;
    private final boolean skipIfNotExists;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        String scopeName = keyspace.getScope();
        String collectionName = keyspace.getCollection();
        BucketOperator bucketOperator = clusterOperator.getBucketOperator(keyspace.getBucket());

        boolean isNotExists = !bucketOperator.hasCollectionInScope(keyspace.getCollection(), keyspace.getScope());
        if (skipIfNotExists && isNotExists) {
            logger.info(format(skipMsg, keyspace.getCollection()));
            return;
        }
        if (isNotExists) {
            throw new CollectionNotExistsException(keyspace.getCollection(), keyspace.getScope());
        }
        bucketOperator.dropCollection(collectionName, scopeName);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        //TODO remove when all statements move to execute with cluster and we refactor NoSqlExecutor
        execute(new ClusterOperator(connection.getCluster()));
    }
}
