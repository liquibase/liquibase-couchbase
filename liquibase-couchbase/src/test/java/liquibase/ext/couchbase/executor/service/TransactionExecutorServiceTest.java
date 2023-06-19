package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import static org.assertj.core.api.Assertions.assertThat;

@MockitoSettings
class TransactionExecutorServiceTest {

    @Mock
    private Cluster cluster;

    @Test
    void Should_return_ReactiveTransactionExecutorService_if_reactive_is_enabled() {
        try (MockedStatic<CouchbaseLiquibaseConfiguration> mockedConfiguration = Mockito.mockStatic(
                CouchbaseLiquibaseConfiguration.class)) {
            mockedConfiguration.when(CouchbaseLiquibaseConfiguration::isReactiveTransactions)
                    .thenReturn(true);

            TransactionExecutorService transactionExecutorService = TransactionExecutorService.getExecutor(cluster);
            assertThat(transactionExecutorService).isInstanceOf(ReactiveTransactionExecutorService.class);
        }
    }

    @Test
    void Should_return_PlainTransactionExecutorService_if_reactive_is_disabled() {
        try (MockedStatic<CouchbaseLiquibaseConfiguration> mockedConfiguration = Mockito.mockStatic(
                CouchbaseLiquibaseConfiguration.class)) {
            mockedConfiguration.when(CouchbaseLiquibaseConfiguration::isReactiveTransactions)
                    .thenReturn(false);

            TransactionExecutorService transactionExecutorService = TransactionExecutorService.getExecutor(cluster);
            assertThat(transactionExecutorService).isInstanceOf(PlainTransactionExecutorService.class);
        }
    }

}
