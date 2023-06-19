package liquibase.ext.couchbase.executor.service;

import java.util.function.Function;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ReactiveCluster;
import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.ReactiveTransactions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.ext.couchbase.exception.TransactionalReactiveStatementExecutionException;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import liquibase.ext.couchbase.types.CouchbaseReactiveTransactionAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class ReactiveTransactionExecutorServiceTest {

    @Mock
    private Cluster cluster;
    @Mock
    private ReactiveCluster reactiveCluster;
    @Mock
    private ReactiveTransactions transactions;
    @Mock
    private CouchbaseTransactionStatement transactionStatement;
    @Mock
    private CouchbaseReactiveTransactionAction transactionAction;

    private ReactiveTransactionExecutorService reactiveTransactionExecutorService;

    @BeforeEach
    void setUp() {
        reactiveTransactionExecutorService = new ReactiveTransactionExecutorService(cluster);
    }

    @Test
    void Should_not_execute_empty() {
        reactiveTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster, never()).reactive();
    }

    @Test
    void Should_clear_successfully() {
        reactiveTransactionExecutorService.addStatementIntoQueue(transactionStatement);
        reactiveTransactionExecutorService.clearStatementsQueue();
        reactiveTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster, never()).reactive();
    }

    @Test
    void Should_execute_successfully() {
        when(cluster.reactive()).thenReturn(reactiveCluster);
        when(reactiveCluster.transactions()).thenReturn(transactions);
        when(transactions.run(any(), any())).thenAnswer((arg) -> {
            Function<ReactiveTransactionAttemptContext, Mono<?>> funcArg = arg.getArgument(0);
            return funcArg.apply(mock(ReactiveTransactionAttemptContext.class));
        });
        when(transactionStatement.asTransactionReactiveAction(any())).thenReturn(transactionAction);
        when(transactionAction.apply(any())).thenReturn(Mono.empty());

        reactiveTransactionExecutorService.addStatementIntoQueue(transactionStatement);
        reactiveTransactionExecutorService.executeStatementsInTransaction();

        verify(cluster).reactive();
        verify(transactions).run(any(), any());
    }

    @Test
    void Should_catch_TransactionalStatementExecutionException() {
        when(cluster.reactive()).thenReturn(reactiveCluster);
        when(reactiveCluster.transactions()).thenReturn(transactions);
        TransactionFailedException mockedException = mock(TransactionFailedException.class);
        when(transactions.run(any(), any())).thenThrow(mockedException);

        reactiveTransactionExecutorService.addStatementIntoQueue(transactionStatement);

        assertThatExceptionOfType(TransactionalReactiveStatementExecutionException.class)
                .isThrownBy(() -> reactiveTransactionExecutorService.executeStatementsInTransaction());

        verify(cluster).reactive();
        verify(transactions).run(any(), any());
    }

}
