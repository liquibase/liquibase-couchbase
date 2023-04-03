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

    public abstract void execute(ClusterOperator clusterOperator);

}
