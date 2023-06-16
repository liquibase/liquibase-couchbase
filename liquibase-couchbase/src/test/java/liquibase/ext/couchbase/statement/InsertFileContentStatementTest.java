package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.ImportFile;
import liquibase.ext.couchbase.types.ImportType;
import liquibase.ext.couchbase.types.KeyProviderType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InsertFileContentStatementTest {

    private final TransactionAttemptContext transaction = mock(TransactionAttemptContext.class);
    private final ReactiveTransactionAttemptContext reactiveTransactionAttemptContext =
            mock(ReactiveTransactionAttemptContext.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final CollectionOperator collectionOperator = mock(CollectionOperator.class);

    private final ImportFile importFile = mock(ImportFile.class);

    @BeforeEach
    public void configure() {
        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collectionOperator);

        when(importFile.getImportType()).thenReturn(ImportType.LIST);
        when(importFile.getKeyProviderType()).thenReturn(KeyProviderType.DEFAULT);
    }

    @Test
    void Should_execute_in_transaction() {
        InsertFileContentStatement statement = new InsertFileContentStatement(TEST_KEYSPACE, importFile);

        statement.doInTransaction(transaction, clusterOperator);

        verify(collectionOperator).insertDocsTransactionally(eq(transaction), anyList());
    }

    @Test
    void Should_execute_in_transaction_reactive() {
        InsertFileContentStatement statement = new InsertFileContentStatement(TEST_KEYSPACE, importFile);
        Flux<TransactionGetResult> mockedPublisher = Flux.empty();
        when(collectionOperator.insertDocsTransactionallyReactive(eq(reactiveTransactionAttemptContext),
                anyList())).thenReturn(mockedPublisher);

        Publisher<?> resultPublisher =
                statement.doInTransactionReactive(reactiveTransactionAttemptContext, clusterOperator);
        assertThat(resultPublisher).isEqualTo(mockedPublisher);

        verify(collectionOperator).insertDocsTransactionallyReactive(eq(reactiveTransactionAttemptContext), anyList());
    }
}