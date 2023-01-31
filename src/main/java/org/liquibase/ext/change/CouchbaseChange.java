package org.liquibase.ext.change;

import org.liquibase.ext.database.CouchbaseLiquibaseDatabase;

import liquibase.change.AbstractChange;
import liquibase.database.Database;

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
