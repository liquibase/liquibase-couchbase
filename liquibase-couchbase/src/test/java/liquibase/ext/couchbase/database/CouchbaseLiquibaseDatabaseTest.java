package liquibase.ext.couchbase.database;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.database.Database;
import liquibase.database.DatabaseConnection;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_SHORT_NAME;
import static liquibase.ext.couchbase.database.Constants.DEFAULT_PORT;
import static liquibase.servicelocator.PrioritizedService.PRIORITY_DATABASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

class CouchbaseLiquibaseDatabaseTest {

    private static final String DB_URL = "couchbase://127.0.0.1";
    private final CouchbaseConnection connection = spy(CouchbaseConnection.class);
    private final Database database = mock(Database.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Cluster cluster = mock(Cluster.class);

    private CouchbaseLiquibaseDatabase couchbaseLiquibaseDatabase;

    @BeforeEach
    public void configure() {
        couchbaseLiquibaseDatabase = new CouchbaseLiquibaseDatabase("user", "password", DB_URL);
        couchbaseLiquibaseDatabase.getConnection();

        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(connection.getDatabase()).thenReturn(bucket);
    }

    @Test
    void Should_create_connection_correctly() {
        assertThat(couchbaseLiquibaseDatabase.getDefaultDatabaseProductName()).isEqualTo(COUCHBASE_PRODUCT_NAME);
        assertThat(couchbaseLiquibaseDatabase.getDefaultDriver(DB_URL)).isEqualTo(CouchbaseStubDriver.class.getName());
        assertThat(couchbaseLiquibaseDatabase.supportsTablespaces()).isEqualTo(false);
        assertThat(couchbaseLiquibaseDatabase.supportsInitiallyDeferrableColumns()).isEqualTo(false);
        assertThat(couchbaseLiquibaseDatabase.getDefaultPort()).isEqualTo(DEFAULT_PORT);
        assertThat(couchbaseLiquibaseDatabase.getShortName()).isEqualTo(COUCHBASE_PRODUCT_SHORT_NAME);
        assertThat(couchbaseLiquibaseDatabase.getPriority()).isEqualTo(PRIORITY_DATABASE);
    }

    @Test
    void Should_throw_exception_with_invalid_db_url() {
        String invalidURL = "aaa";

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> couchbaseLiquibaseDatabase.getDefaultDriver(invalidURL))
                .withMessage("%s driver is not supported", invalidURL);
    }

    @Test
    @SneakyThrows
    void Should_check_database_implementation_successfully() {
        DatabaseConnection databaseConnection = mock(DatabaseConnection.class);

        when(databaseConnection.getDatabaseProductName()).thenReturn(COUCHBASE_PRODUCT_NAME);

        assertThat(couchbaseLiquibaseDatabase.isCorrectDatabaseImplementation(databaseConnection)).isTrue();
    }

    @Test
    @SneakyThrows
    void Should_check_database_implementation_unsuccessfully() {
        DatabaseConnection databaseConnection = mock(DatabaseConnection.class);

        when(databaseConnection.getDatabaseProductName()).thenReturn("Mocked");

        assertThat(couchbaseLiquibaseDatabase.isCorrectDatabaseImplementation(databaseConnection)).isFalse();
    }
}