package liquibase.ext.couchbase.database;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.ext.couchbase.executor.service.TransactionExecutorService;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Driver;
import java.util.Properties;

import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRIORITY;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.servicelocator.PrioritizedService.PRIORITY_DEFAULT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CouchbaseConnectionTest {

    private static final String DB_URL = "couchbase://127.0.0.1";
    private final CouchbaseConnection connection = spy(CouchbaseConnection.class);
    private final Database database = mock(Database.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Cluster cluster = mock(Cluster.class);
    private final Driver driver = mock(Driver.class);

    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(connection.getDatabase()).thenReturn(bucket);
    }

    @Test
    @SneakyThrows
    void Should_open_connection_correctly() {
        Properties driverProperties = buildDriverProperties();

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
    void Should_open_connection_on_bucket() {
        try (MockedStatic<Cluster> mockedStatic = Mockito.mockStatic(Cluster.class)) {
            String bucketName = "bucket";

            mockedStatic.when(() -> Cluster.connect(anyString(), any())).thenReturn(cluster);

            Properties driverProperties = buildDriverProperties();
            driverProperties.setProperty("bucket", bucketName);

            connection.open(DB_URL, driver, driverProperties);

            verify(cluster).bucket(bucketName);
        }
    }

    @Test
    @SneakyThrows
    void Should_close_connection_correctly() {
        Properties driverProperties = buildDriverProperties();

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

    @Test
    @SneakyThrows
    void Should_catch_and_wrap_exception_when_cluster_cant_connect() {
        try (MockedStatic<Cluster> mockedStatic = Mockito.mockStatic(Cluster.class)) {
            mockedStatic.when(() -> Cluster.connect(anyString(), any()))
                    .thenThrow(new RuntimeException("Mocked"));
            Properties driverProperties = buildDriverProperties();

            assertThatExceptionOfType(DatabaseException.class)
                    .isThrownBy(() -> connection.open(DB_URL, driver, driverProperties))
                    .withMessage("Could not open connection to database: %s", DB_URL);

            mockedStatic.verify(() -> Cluster.connect(anyString(), any()));
        }
    }

    @Test
    void Should_support_correct_url() {
        assertThat(connection.supports("couchbase://127.0.0.1")).isTrue();
    }

    @Test
    void Should_not_support_incorrect_url() {
        assertThat(connection.supports("NOT_COUCHBASE://127.0.0.1")).isFalse();
    }

    @Test
    @SneakyThrows
    void Should_rollback_transaction_using_transaction_executor() {
        try (MockedStatic<Cluster> mockedCluster = Mockito.mockStatic(Cluster.class)) {
            try (MockedStatic<TransactionExecutorService> mockedTransactionExecutorService = Mockito.mockStatic(
                    TransactionExecutorService.class)) {
                TransactionExecutorService transactionExecutorService = mock(TransactionExecutorService.class);

                String bucketName = "bucket";

                mockedCluster.when(() -> Cluster.connect(anyString(), any())).thenReturn(cluster);
                mockedTransactionExecutorService.when(() -> TransactionExecutorService.getExecutor(cluster)).thenReturn(
                        transactionExecutorService);

                Properties driverProperties = buildDriverProperties();
                driverProperties.setProperty("bucket", bucketName);

                connection.open(DB_URL, driver, driverProperties);

                connection.rollback();

                verify(transactionExecutorService).clearStatementsQueue();
            }
        }
    }

    @Test
    @SneakyThrows
    void Should_commit_transaction_using_transaction_executor() {
        try (MockedStatic<Cluster> mockedCluster = Mockito.mockStatic(Cluster.class)) {
            try (MockedStatic<TransactionExecutorService> mockedTransactionExecutorService = Mockito.mockStatic(
                    TransactionExecutorService.class)) {
                TransactionExecutorService transactionExecutorService = mock(TransactionExecutorService.class);

                String bucketName = "bucket";

                mockedCluster.when(() -> Cluster.connect(anyString(), any())).thenReturn(cluster);
                mockedTransactionExecutorService.when(() -> TransactionExecutorService.getExecutor(cluster)).thenReturn(
                        transactionExecutorService);

                Properties driverProperties = buildDriverProperties();
                driverProperties.setProperty("bucket", bucketName);

                connection.open(DB_URL, driver, driverProperties);

                connection.commit();

                verify(transactionExecutorService).executeStatementsInTransaction();
            }
        }
    }

    private Properties buildDriverProperties() {
        Properties driverProperties = new Properties();
        driverProperties.setProperty("user", "user");
        driverProperties.setProperty("password", "password");
        return driverProperties;
    }
}