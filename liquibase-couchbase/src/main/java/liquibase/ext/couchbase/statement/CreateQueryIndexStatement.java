package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.IndexExistsException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.lang.String.format;

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
    private final boolean ignoreIfExists;
    private final int numReplicas;
    private final List<Field> fields;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        boolean exists = clusterOperator.indexExists(indexName, keyspace);

        if (ignoreIfExists && exists) {
            logger.info(format(existsMsg, indexName));
            return;
        }

        if (exists) {
            throw new IndexExistsException(indexName);
        }
        CreateQueryIndexOptions options = CreateQueryIndexOptions.createQueryIndexOptions()
                .deferred(deferred)
                .numReplicas(numReplicas);
        clusterOperator.createQueryIndex(indexName, keyspace, fields, options);
    }
}