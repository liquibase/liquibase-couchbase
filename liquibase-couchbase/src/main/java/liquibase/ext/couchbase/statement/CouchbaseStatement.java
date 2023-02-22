package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.statement.SqlStatement;

/**
 * A baseline for all DDL Couchbase statements. Uses {@link ClusterOperator} to execute statements instead of {@link CouchbaseConnection}.
 * @see SqlStatement
 * @see ClusterOperator
 */

public abstract class CouchbaseStatement extends NoSqlStatement {

    public void execute(ClusterOperator clusterOperator){}

    /**
     * Backward compatibility, later we will remove that
     */
    public void execute(CouchbaseConnection connection) {
        execute(new ClusterOperator(connection.getCluster()));
    }

}
