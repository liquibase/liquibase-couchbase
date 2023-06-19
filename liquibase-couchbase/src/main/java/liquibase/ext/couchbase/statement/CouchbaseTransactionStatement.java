package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.executor.TransactionalStatementQueue;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.CouchbaseReactiveTransactionAction;
import liquibase.ext.couchbase.types.CouchbaseTransactionAction;
import liquibase.statement.SqlStatement;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;

/**
 * A baseline for all Couchbase statements which should be executed inside one transaction (DML). Uses {@link ClusterOperator} to execute
 * statements instead of {@link CouchbaseConnection}. Returns {@link Consumer} to execute it later in transaction.
 * @see TransactionalStatementQueue
 * @see CouchbaseConnection
 * @see SqlStatement
 * @see ClusterOperator
 */

public abstract class CouchbaseTransactionStatement extends NoSqlStatement {

    public CouchbaseTransactionAction asTransactionAction(ClusterOperator clusterOperator) {
        return transaction -> doInTransaction(transaction, clusterOperator);
    }

    public CouchbaseReactiveTransactionAction asTransactionReactiveAction(ClusterOperator clusterOperator) {
        return transaction -> doInTransactionReactive(transaction, clusterOperator);
    }

    public abstract void doInTransaction(TransactionAttemptContext transaction,
                                         ClusterOperator clusterOperator);

    public abstract Publisher<?> doInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                         ClusterOperator clusterOperator);

}
