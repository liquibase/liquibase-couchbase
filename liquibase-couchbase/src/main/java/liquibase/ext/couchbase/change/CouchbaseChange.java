package liquibase.ext.couchbase.change;

import liquibase.change.Change;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;

import liquibase.change.AbstractChange;
import liquibase.database.Database;
import lombok.NoArgsConstructor;

/**
 *
 * Base class for all Couchbase changes.
 *
 * @apiNote All ancestors should have a no-arg constructor
 * @see AbstractChange
 * @see Change
 * @see NoArgsConstructor
 *
 */

public abstract class CouchbaseChange extends AbstractChange {

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
}
