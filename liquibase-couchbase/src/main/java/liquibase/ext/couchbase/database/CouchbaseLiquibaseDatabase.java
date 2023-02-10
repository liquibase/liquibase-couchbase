package liquibase.ext.couchbase.database;

import java.sql.Driver;
import java.util.Properties;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PREFIX;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_SHORT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_SSL_PREFIX;
import static liquibase.ext.couchbase.database.Constants.DEFAULT_PORT;

/**
 * Represent instance of {@link com.couchbase.client.java.Cluster}
 */
@NoArgsConstructor
@AllArgsConstructor
public class CouchbaseLiquibaseDatabase extends AbstractJdbcDatabase {

    private ConnectionData connectionData;

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
        throw new IllegalArgumentException(url + " driver is not supported");
    }

    @Override
    @SneakyThrows
    public synchronized CouchbaseConnection getConnection() {
        CouchbaseConnection connection = (CouchbaseConnection) super.getConnection();
        if (connection != null) {
            return connection;
        }

        CouchbaseConnection couchbaseConnection = new CouchbaseConnection();
        Properties properties = new Properties();
        properties.put("user", connectionData.getUser());
        properties.put("password", connectionData.getPassword());
        Driver driver = new CouchbaseClientDriver();
        couchbaseConnection.open(connectionData.getConnectionString(), driver, properties);
        setConnection(couchbaseConnection);
        return couchbaseConnection;
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
