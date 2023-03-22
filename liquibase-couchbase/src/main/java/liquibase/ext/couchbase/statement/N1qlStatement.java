package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.reactivestreams.Publisher;

/**
 * A statement to execute n1ql(sql++) queries
 *
 * @see CouchbaseTransactionStatement
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class N1qlStatement extends CouchbaseTransactionStatement {

    private final List<String> queries;
    private final boolean transactional;

    @Override
    public void doInTransaction(TransactionAttemptContext transaction, ClusterOperator clusterOperator) {
        if (transactional) {
            clusterOperator.executeN1ql(transaction, queries);
        } else {
            clusterOperator.executeN1ql(queries);
        }
    }

    @Override
    public Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                ClusterOperator clusterOperator) {
        // TODO investigate
        return null;
    }
}
