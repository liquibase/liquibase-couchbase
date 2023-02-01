package liquibase.ext.statement;

import liquibase.ext.database.CouchbaseLiquibaseDatabase;
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

    public abstract void execute(CouchbaseLiquibaseDatabase database);
}
