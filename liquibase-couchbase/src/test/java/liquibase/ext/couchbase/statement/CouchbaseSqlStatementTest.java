package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.resource.Resource;
import liquibase.sdk.resource.MockResource;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CouchbaseSqlStatementTest {

    private final TransactionAttemptContext transaction = mock(TransactionAttemptContext.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final Resource resource = new MockResource("test", "Some Content");


    @Test
    void Should_execute_in_transaction_if_transactional() {
        CouchbaseSqlStatement statement = new CouchbaseSqlStatement(resource, true);

        statement.doInTransaction(transaction, clusterOperator);

        verify(clusterOperator).executeSql(eq(transaction), anyList());
    }

    @Test
    void Should_execute_without_transaction_if_not_transactional() {
        CouchbaseSqlStatement statement = new CouchbaseSqlStatement(resource, false);

        statement.doInTransaction(transaction, clusterOperator);

        verify(clusterOperator).executeSql(anyList());
    }
}