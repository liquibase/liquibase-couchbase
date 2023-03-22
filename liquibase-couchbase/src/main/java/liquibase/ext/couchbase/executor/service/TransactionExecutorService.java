package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;

import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.IS_REACTIVE_TRANSACTIONS;

/**
 * Parent class for transaction executors.
 */
public abstract class TransactionExecutorService {

    protected final Cluster cluster;
    protected final ClusterOperator clusterOperator;

    protected TransactionExecutorService(Cluster cluster) {
        this.cluster = cluster;
        this.clusterOperator = new ClusterOperator(cluster);
    }

    public abstract void addStatementIntoQueue(CouchbaseTransactionStatement transactionStatement);

    public abstract void executeStatementsInTransaction();

    public abstract void clearStatementsQueue();

    public static TransactionExecutorService getExecutor(Cluster cluster) {
        return IS_REACTIVE_TRANSACTIONS.getCurrentValue() ? new ReactiveTransactionExecutorService(cluster)
                : new PlainTransactionExecutorService(cluster);
    }

}
