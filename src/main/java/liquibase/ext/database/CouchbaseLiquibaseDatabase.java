package liquibase.ext.database;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import lombok.NoArgsConstructor;

import static liquibase.ext.database.Constants.*;

@NoArgsConstructor
public class CouchbaseLiquibaseDatabase extends AbstractJdbcDatabase {



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
        if (url.startsWith(COUCHBASE_PREFIX) ||
                url.startsWith(COUCHBASE_SSL_PREFIX)) {
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
        return DEFAULT_PORT;
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
