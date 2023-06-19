package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.transactions.Transactions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.ext.couchbase.exception.TransactionalStatementExecutionException;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class PlainTransactionExecutorServiceTest {

    @Mock
    private Cluster cluster;
    @Mock
    private Transactions transactions;
    @Mock
    private CouchbaseTransactionStatement couchbaseTransactionStatement;

    private PlainTransactionExecutorService plainTransactionExecutorService;

    @BeforeEach
    void setUp() {
        plainTransactionExecutorService = new PlainTransactionExecutorService(cluster);
    }

    @Test
    void Should_not_execute_empty() {
        plainTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster, never()).transactions();
    }

    @Test
    void Should_clear_successfully() {
        plainTransactionExecutorService.addStatementIntoQueue(couchbaseTransactionStatement);
        plainTransactionExecutorService.clearStatementsQueue();
        plainTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster, never()).transactions();
    }

    @Test
    void Should_execute_successfully() {
        when(cluster.transactions()).thenReturn(transactions);

        plainTransactionExecutorService.addStatementIntoQueue(couchbaseTransactionStatement);

        plainTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster).transactions();
        verify(transactions).run(any(), any());
    }

    @Test
    void Should_catch_TransactionalStatementExecutionException() {
        when(cluster.transactions()).thenReturn(transactions);
        TransactionFailedException mockedException = mock(TransactionFailedException.class);
        when(transactions.run(any(), any())).thenThrow(mockedException);

        plainTransactionExecutorService.addStatementIntoQueue(couchbaseTransactionStatement);

        assertThatExceptionOfType(TransactionalStatementExecutionException.class)
                .isThrownBy(() -> plainTransactionExecutorService.executeStatementsInTransaction());

        verify(cluster).transactions();
        verify(transactions).run(any(), any());
    }

}
