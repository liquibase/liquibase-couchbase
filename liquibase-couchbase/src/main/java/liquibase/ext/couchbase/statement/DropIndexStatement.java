package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import liquibase.Scope;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.IndexNotExistsException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

/**
 * A statement to drop secondary index for a keyspace
 *
 * @see CouchbaseStatement
 * @see DropQueryIndexOptions
 * @see Keyspace
 */

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropIndexStatement extends CouchbaseStatement {
    private static final String notExistsMsg = "Index %s not exists, skipping removing";
    private final Logger logger = Scope.getCurrentScope().getLog(CreateQueryIndexStatement.class);

    private final boolean ignoreIfNotExists;
    private final String indexName;
    private final Keyspace keyspace;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        boolean notExists = !clusterOperator.indexExists(indexName, keyspace);

        if (ignoreIfNotExists && notExists) {
            logger.info(format(notExistsMsg, indexName));
            return;
        }

        if (notExists) {
            throw new IndexNotExistsException(indexName);
        }

        clusterOperator.dropIndex(indexName, keyspace);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        execute(new ClusterOperator(connection.getCluster()));
    }
}
