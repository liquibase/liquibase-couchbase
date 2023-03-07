package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.statement.SqlStatement;

/**
 * Base class for all conditional statements, uses {@link CouchbaseConnection} to execute check of the condition
 * @see SqlStatement
 * @see CouchbaseConnection
 */

public abstract class CouchbaseConditionalStatement extends NoSqlStatement {

    public abstract boolean isTrue(CouchbaseConnection connection);
}
