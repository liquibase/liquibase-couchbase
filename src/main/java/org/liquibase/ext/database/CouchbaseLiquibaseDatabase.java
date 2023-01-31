package org.liquibase.ext.database;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;

public class CouchbaseLiquibaseDatabase extends AbstractJdbcDatabase {


    @Override
    protected String getDefaultDatabaseProductName() {
        return null;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        return false;
    }

    @Override
    public String getDefaultDriver(String url) {
        return null;
    }

    @Override
    public String getShortName() {
        return null;
    }

    @Override
    public Integer getDefaultPort() {
        return null;
    }

    @Override
    public boolean supportsInitiallyDeferrableColumns() {
        return false;
    }

    @Override
    public boolean supportsTablespaces() {
        return false;
    }

    @Override
    public int getPriority() {
        return 0;
    }
}
