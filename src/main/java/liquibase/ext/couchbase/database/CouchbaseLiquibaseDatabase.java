package liquibase.ext.couchbase.database;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CouchbaseLiquibaseDatabase extends AbstractJdbcDatabase implements Database {

    public static final String COUCHBASE_PRODUCT_NAME = "Couchbase";
    public static final String COUCHBASE_PRODUCT_SHORT_NAME = "couchbase";

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }


    @Override
    protected String getDefaultDatabaseProductName() {
        return COUCHBASE_PRODUCT_NAME;
    }

    @Override
    public boolean isCorrectDatabaseImplementation(DatabaseConnection conn) throws DatabaseException {
        return getDatabaseProductName().equals(conn.getDatabaseProductName());
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith(CouchbaseConnection.COUCHBASE_PREFIX) ||
                url.startsWith(CouchbaseConnection.COUCHBASE_SSL_PREFIX)) {
            return CouchbaseClientDriver.class.getName();
        }
        return null;
    }

    @Override
    public String getShortName() {
        return COUCHBASE_PRODUCT_SHORT_NAME;
    }

    @Override
    public Integer getDefaultPort() {
        return CouchbaseConnection.DEFAULT_PORT;
    }

    @Override
    public boolean supportsInitiallyDeferrableColumns() {
        return false;
    }

    @Override
    public boolean supportsTablespaces() {
        return false;
    }
}
