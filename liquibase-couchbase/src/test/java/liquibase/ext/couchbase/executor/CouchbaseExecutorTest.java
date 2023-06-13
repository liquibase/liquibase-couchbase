package liquibase.ext.couchbase.executor;

import com.couchbase.client.java.Cluster;
import liquibase.Scope;
import liquibase.exception.DatabaseException;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.exception.StatementExecutionException;
import liquibase.ext.couchbase.executor.service.TransactionExecutorService;
import liquibase.ext.couchbase.statement.CouchbaseStatement;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import liquibase.logging.Logger;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import static liquibase.ext.couchbase.executor.CouchbaseExecutor.EXECUTOR_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class CouchbaseExecutorTest {

    @Mock
    private CouchbaseLiquibaseDatabase database;
    @Mock
    private Cluster cluster;
    @Mock
    private CouchbaseConnection connection;
    @Mock
    private TransactionExecutorService transactionExecutorService;
    @Mock
    private SqlStatement sqlStatement;
    @Mock
    private CouchbaseStatement couchbaseStatement;
    @Mock
    private CouchbaseTransactionStatement couchbaseTransactionStatement;

    private CouchbaseExecutor couchbaseExecutor;

    @BeforeEach
    void setUp() {
        couchbaseExecutor = new CouchbaseExecutor();
        couchbaseExecutor.setDatabase(database);
    }

    @Test
    void Should_support_couchbase() {
        assertThat(couchbaseExecutor.supports(database)).isTrue();
    }

    @Test
    void Should_throw_if_statement_not_couchbase() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> couchbaseExecutor.execute(sqlStatement))
                .withMessage("Couchbase cannot execute %s statements", sqlStatement.getClass().getName());
    }

    @Test
    void Should_add_transaction_statement_to_queue() throws DatabaseException {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getTransactionExecutorService()).thenReturn(transactionExecutorService);

        couchbaseExecutor.execute(couchbaseTransactionStatement);

        verify(transactionExecutorService).addStatementIntoQueue(couchbaseTransactionStatement);
    }

    @Test
    void Should_execute_statement() throws DatabaseException {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);

        couchbaseExecutor.execute(couchbaseStatement);

        verify(couchbaseStatement).execute(any());
    }

    @Test
    void Should_wrap_exception() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);

        doThrow(new RuntimeException("Mocked")).when(couchbaseStatement).execute(any());

        assertThatExceptionOfType(StatementExecutionException.class)
                .isThrownBy(() -> couchbaseExecutor.execute(couchbaseStatement));
    }

    @Test
    void Should_send_comment_to_log() {
        try (MockedStatic<Scope> mockedStatic = Mockito.mockStatic(Scope.class)) {
            String comment = "comment";
            Scope currentScope = mock(Scope.class);
            try (Logger logger = mock(Logger.class)) {
                mockedStatic.when(Scope::getCurrentScope).thenReturn(currentScope);
                when(currentScope.getLog(any())).thenReturn(logger);
                couchbaseExecutor = new CouchbaseExecutor();
                couchbaseExecutor.setDatabase(database);

                assertThatCode(() -> couchbaseExecutor.comment(comment)).doesNotThrowAnyException();

                verify(logger).info(comment);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    void Verify_priority() {
        assertThat(couchbaseExecutor.getPriority()).isEqualTo(PRIORITY_SPECIALIZED);
    }

    @Test
    void Verify_name() {
        assertThat(couchbaseExecutor.getName()).isEqualTo(EXECUTOR_NAME);
    }
}
