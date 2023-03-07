package liquibase.ext.couchbase.lockservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import liquibase.Scope;
import liquibase.executor.Executor;
import liquibase.executor.ExecutorService;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.lockservice.LockServiceFactory;
import lombok.SneakyThrows;
import static liquibase.ext.couchbase.executor.CouchbaseExecutor.EXECUTOR_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class CouchbaseLockServiceTest {

    @Mock
    private Executor executor;

    @Mock
    private CouchbaseConnection connection;

    @Mock
    private ContextServiceProvider serviceProvider;

    private CouchbaseLiquibaseDatabase database;

    private CouchbaseLockService lockService;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        database = new CouchbaseLiquibaseDatabase();
        database.setConnection(connection);
        lockService = new CouchbaseLockService();
        lockService.setDatabase(database);
        lockService.setServiceProvider(serviceProvider);
        reset();
    }

    @AfterEach
    void tearDown() {
        reset();
    }

    private void reset() {
        LockServiceFactory.reset();
        Scope.getCurrentScope().getSingleton(ExecutorService.class).setExecutor(EXECUTOR_NAME, database, executor);
        Scope.getCurrentScope().getSingleton(ExecutorService.class).reset();
    }

    @Test
    @DisplayName("Test priority of lock service")
    void testPriority() {
        assertThat(lockService.getPriority()).isEqualTo(PRIORITY_SPECIALIZED);
    }

    @Test
    @DisplayName("Test lock service supports CouchbaseLiquibaseDatabase")
    void testSupport() {
        assertTrue(lockService.supports(new CouchbaseLiquibaseDatabase()));
        assertTrue(lockService.supports(database));
    }

}
