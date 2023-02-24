package liquibase.ext.couchbase.statement;

import liquibase.statement.SqlStatement;

/**
 * A baseline for all Couchbase statements.
 */
public abstract class NoSqlStatement implements SqlStatement {

    @Override
    public boolean skipOnUnsupported() {
        return false;
    }

    @Override
    public boolean continueOnError() {
        return false;
    }

}
