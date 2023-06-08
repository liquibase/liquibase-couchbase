package liquibase.ext.couchbase.database;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.database.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_SHORT_NAME;
import static liquibase.ext.couchbase.database.Constants.DEFAULT_PORT;
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

    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(connection.getDatabase()).thenReturn(bucket);
    }

    @Test
    void Should_create_connection_correctly() {
        CouchbaseLiquibaseDatabase couchbaseLiquibaseDatabase = new CouchbaseLiquibaseDatabase("user", "password", DB_URL);
        couchbaseLiquibaseDatabase.getConnection();

        assertThat(couchbaseLiquibaseDatabase.getDefaultDatabaseProductName()).isEqualTo(COUCHBASE_PRODUCT_NAME);
        assertThat(couchbaseLiquibaseDatabase.getDefaultDriver(DB_URL)).isEqualTo(CouchbaseStubDriver.class.getName());
        assertThat(couchbaseLiquibaseDatabase.supportsTablespaces()).isEqualTo(false);
        assertThat(couchbaseLiquibaseDatabase.supportsInitiallyDeferrableColumns()).isEqualTo(false);
        assertThat(couchbaseLiquibaseDatabase.getDefaultPort()).isEqualTo(DEFAULT_PORT);
        assertThat(couchbaseLiquibaseDatabase.getShortName()).isEqualTo(COUCHBASE_PRODUCT_SHORT_NAME);
    }

    @Test
    void Should_throw_exception_with_invalid_db_url() {
        String invalidURL = "aaa";
        CouchbaseLiquibaseDatabase couchbaseLiquibaseDatabase = new CouchbaseLiquibaseDatabase("user", "password", DB_URL);
        couchbaseLiquibaseDatabase.getConnection();

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> couchbaseLiquibaseDatabase.getDefaultDriver(invalidURL))
                .withMessage("%s driver is not supported", invalidURL);
    }
}