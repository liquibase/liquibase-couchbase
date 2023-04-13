package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import liquibase.Scope;
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
 * @see CouchbaseStatement
 * @see ClusterOperator
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
        Collection collection = clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollection(keyspace.getCollection(), keyspace.getScope());
        boolean notExists = !clusterOperator.getCollectionOperator(collection).collectionIndexExists(indexName);

        if (ignoreIfNotExists && notExists) {
            logger.info(format(notExistsMsg, indexName));
            return;
        }

        if (notExists) {
            throw new IndexNotExistsException(indexName);
        }

        clusterOperator.getCollectionOperator(collection).dropCollectionIndex(indexName);
    }
}
