package liquibase.ext.couchbase.change;

import liquibase.change.AbstractChange;
import liquibase.change.Change;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.exception.StatementGenerationException;
import liquibase.statement.SqlStatement;
import lombok.NoArgsConstructor;

/**
 * Base class for all Couchbase changes.
 * @see AbstractChange
 * @see Change
 * @see NoArgsConstructor
 */

public abstract class CouchbaseChange extends AbstractChange {

    public abstract SqlStatement[] generateStatements();

    @Override
    public boolean supports(Database database) {
        return database instanceof CouchbaseLiquibaseDatabase;
    }

    @Override
    public boolean generateStatementsVolatile(Database database) {
        return false;
    }

    @Override
    public boolean generateRollbackStatementsVolatile(Database database) {
        return false;
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        try {
            return generateStatements();
        } catch (Exception e) {
            throw new StatementGenerationException(getClass(), e);
        }
    }

}
