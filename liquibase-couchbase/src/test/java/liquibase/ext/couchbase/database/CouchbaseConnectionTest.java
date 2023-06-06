package liquibase.ext.couchbase.database;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Driver;
import java.util.Properties;

import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRIORITY;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.servicelocator.PrioritizedService.PRIORITY_DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CouchbaseConnectionTest {

    private static final String DB_URL = "couchbase://127.0.0.1";
    private final CouchbaseConnection connection = spy(CouchbaseConnection.class);
    private final Database database = mock(Database.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Cluster cluster = mock(Cluster.class);
    Driver driver = mock(Driver.class);

    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(connection.getDatabase()).thenReturn(bucket);
    }

    @Test
    @SneakyThrows
    void Should_open_connection_correctly() {
        Properties driverProperties = new Properties();
        driverProperties.setProperty("user", "user");
        driverProperties.setProperty("password", "password");

        connection.open(DB_URL, driver, driverProperties);

        assertThat(connection.getConnectionUserName()).isEqualTo("user");
        assertThat(connection.getConnectionString().scheme().name()).isEqualTo("COUCHBASE");
        assertThat(connection.getCatalog()).isEqualTo(StringUtils.EMPTY);
        assertThat(connection.getDatabaseProductName()).isEqualTo(COUCHBASE_PRODUCT_NAME);
        assertThat(connection.getURL()).isEqualTo("127.0.0.1");
        assertThat(connection.isClosed()).isEqualTo(false);
        assertThat(connection.getAutoCommit()).isEqualTo(false);
        assertThat(connection.getPriority()).isEqualTo(PRIORITY_DEFAULT + COUCHBASE_PRIORITY);
        assertThat(connection.getDatabaseProductVersion()).isEqualTo("0");
        assertThat(connection.getDatabaseMajorVersion()).isEqualTo(0);
        assertThat(connection.getDatabaseMinorVersion()).isEqualTo(0);
    }

    @Test
    @SneakyThrows
    void Should_close_connection_correctly() {
        Properties driverProperties = new Properties();
        driverProperties.setProperty("user", "user");
        driverProperties.setProperty("password", "password");

        connection.open(DB_URL, driver, driverProperties);

        assertThat(connection.isClosed()).isEqualTo(false);
        connection.close();
        assertThat(connection.isClosed()).isEqualTo(true);

    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_open_connection_with_invalid_params() {
        Properties driverProperties = new Properties();

        assertThatExceptionOfType(DatabaseException.class)
                .isThrownBy(() -> connection.open(DB_URL, driver, driverProperties))
                .withMessage("Could not open connection to database: %s", DB_URL);
    }

}