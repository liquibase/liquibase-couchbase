package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.reader.SqlFileReader;
import liquibase.resource.Resource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.reactivestreams.Publisher;

import java.util.List;

import static liquibase.Scope.getCurrentScope;

/**
 * A statement to execute n1ql(sql++) queries
 * @see CouchbaseTransactionStatement
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CouchbaseSqlStatement extends CouchbaseTransactionStatement {

    private final Resource resource;
    private final boolean transactional;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        SqlFileReader sqlFileReader = getCurrentScope().getSingleton(SqlFileReader.class);
        List<String> queries = sqlFileReader.readQueries(resource);
        if (transactional) {
            clusterOperator.executeSql(transaction, queries);
            return;
        }

        clusterOperator.executeSql(queries);
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                ClusterOperator clusterOperator) {
        // TODO https://weigandt-consulting.atlassian.net/browse/COS-179
        return null;
    }
}
