package liquibase.ext.couchbase.statement;

import java.util.HashSet;
import java.util.Set;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RemoveDocumentsStatementTest {

    private final TransactionAttemptContext transaction = mock(TransactionAttemptContext.class);
    private final ReactiveTransactionAttemptContext reactiveTransactionAttemptContext =
            mock(ReactiveTransactionAttemptContext.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final CollectionOperator collectionOperator = mock(CollectionOperator.class);

    @BeforeEach
    public void configure() {
        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collectionOperator);
    }

    @Test
    void Should_execute_in_transaction() {
        Set<Id> ids = new HashSet<>();
        ids.add(new Id("id"));
        RemoveDocumentsStatement statement = new RemoveDocumentsStatement(TEST_KEYSPACE, ids);

        statement.asTransactionAction(clusterOperator).accept(transaction);

        verify(collectionOperator).removeDocsTransactionally(transaction, ids);
    }

    @Test
    void Should_execute_in_transaction_reactive() {
        Set<Id> ids = new HashSet<>();
        ids.add(new Id("id"));
        RemoveDocumentsStatement statement = new RemoveDocumentsStatement(TEST_KEYSPACE, ids);
        Flux<TransactionGetResult> mockedPublisher = Flux.empty();
        when(collectionOperator.removeDocsTransactionallyReactive(reactiveTransactionAttemptContext, ids)).thenReturn(
                mockedPublisher);

        Publisher<?> resultPublisher =
                statement.asTransactionReactiveAction(clusterOperator).apply(reactiveTransactionAttemptContext);
        assertThat(resultPublisher).isEqualTo(mockedPublisher);

        verify(collectionOperator).removeDocsTransactionallyReactive(reactiveTransactionAttemptContext, ids);
    }
}