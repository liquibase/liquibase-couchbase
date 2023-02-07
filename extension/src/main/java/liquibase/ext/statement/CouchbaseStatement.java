package liquibase.ext.statement;

import liquibase.ext.database.CouchbaseConnection;
import liquibase.statement.SqlStatement;

public abstract class CouchbaseStatement implements SqlStatement {
    @Override
    public boolean skipOnUnsupported() {
        return false;
    }

    @Override
    public boolean continueOnError() {
        return false;
    }

    public abstract void execute(CouchbaseConnection connection);
}
