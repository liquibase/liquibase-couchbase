package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Set;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.WARN)
class RemoveDocumentsSqlQueryStatementTest {

    private final Set<Id> ids = singleton(new Id("id"));
    private static final String SQL_PLUS_PLUS_QUERY = "sqlPlusPlusQuery";

    @Mock
    private TransactionAttemptContext transaction;
    @Mock
    private ReactiveTransactionAttemptContext reactiveTransactionAttemptContext;
    @Mock
    private ClusterOperator clusterOperator;
    @Mock
    private BucketOperator bucketOperator;
    @Mock
    private CollectionOperator collectionOperator;

    @BeforeEach
    public void configure() {
        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(collectionOperator);
    }

    @Test
    void Should_execute_in_transaction() {
        RemoveDocumentsSqlQueryStatement statement = new RemoveDocumentsSqlQueryStatement(TEST_KEYSPACE, ids, SQL_PLUS_PLUS_QUERY);

        statement.asTransactionAction(clusterOperator).accept(transaction);

        verify(collectionOperator).removeDocsTransactionally(transaction, ids);
    }

    @Test
    void Should_execute_in_transaction_reactive() {
        RemoveDocumentsSqlQueryStatement statement = new RemoveDocumentsSqlQueryStatement(TEST_KEYSPACE, ids, SQL_PLUS_PLUS_QUERY);
        Flux<TransactionGetResult> mockedPublisher = Flux.empty();
        when(collectionOperator.removeDocsTransactionallyReactive(reactiveTransactionAttemptContext, ids)).thenReturn(mockedPublisher);

        Publisher<?> resultPublisher = statement.asTransactionReactiveAction(clusterOperator).apply(reactiveTransactionAttemptContext);
        assertThat(resultPublisher).isEqualTo(mockedPublisher);

        verify(collectionOperator).removeDocsTransactionallyReactive(reactiveTransactionAttemptContext, ids);
    }
}