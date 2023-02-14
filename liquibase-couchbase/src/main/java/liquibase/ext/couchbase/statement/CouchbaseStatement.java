package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.statement.SqlStatement;

/**
 *
 * A baseline for all Couchbase statements. Uses {@link ClusterOperator} to execute statements instead of
 * {@link CouchbaseConnection}.
 *
 * @see SqlStatement
 * @see ClusterOperator
 *
 */

public abstract class CouchbaseStatement implements SqlStatement {

    @Override
    public boolean skipOnUnsupported() {
        return false;
    }

    @Override
    public boolean continueOnError() {
        return false;
    }

    public void execute(ClusterOperator clusterOperator) {

    }

    /**
     * Backward compatibility, later we will remove that
     */
    public abstract void execute(CouchbaseConnection connection);

}
