package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;

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
        return CouchbaseLiquibaseConfiguration.isReactiveTransactions() ? new ReactiveTransactionExecutorService(cluster)
                : new PlainTransactionExecutorService(cluster);
    }

}
