package liquibase.ext.couchbase.database;

import liquibase.database.AbstractJdbcDatabase;
import liquibase.database.DatabaseConnection;
import liquibase.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Properties;

import static java.util.Optional.ofNullable;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PREFIX;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_SHORT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_SSL_PREFIX;
import static liquibase.ext.couchbase.database.Constants.DEFAULT_PORT;

/**
 * Represents instance of {@link com.couchbase.client.java.Cluster}.<br><br>
 */
@RequiredArgsConstructor
public class CouchbaseLiquibaseDatabase extends AbstractJdbcDatabase {

    private final ConnectionData connectionData;

    @Setter(onMethod = @__( {@Override}))
    private DatabaseConnection connection;

    public CouchbaseLiquibaseDatabase() {
        connectionData = new ConnectionData(
                "Administrator",
                "password",
                "couchbase://127.0.0.1"
        );
    }

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
        return conn.getDatabaseProductName().contains(getDatabaseProductName());
    }

    @Override
    public String getDefaultDriver(String url) {
        if (url.startsWith(COUCHBASE_PREFIX) ||
                url.startsWith(COUCHBASE_SSL_PREFIX)) {
            return CouchbaseStubDriver.class.getName();
        }
        throw new IllegalArgumentException(url + " driver is not supported");
    }

    @Override
    public synchronized CouchbaseConnection getConnection() {
        return ofNullable(connection)
                .map(CouchbaseConnection.class::cast)
                .orElseGet(this::getCouchbaseConnection);
    }

    @SneakyThrows
    private CouchbaseConnection getCouchbaseConnection() {
        CouchbaseConnection couchbaseConnection = new CouchbaseConnection();
        Properties properties = new Properties();
        properties.put("user", connectionData.getUser());
        properties.put("password", connectionData.getPassword());
        couchbaseConnection.open(connectionData.getConnectionString(), null, properties);
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
