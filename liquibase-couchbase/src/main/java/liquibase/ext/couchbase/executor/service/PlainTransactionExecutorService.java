package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.TransactionalStatementExecutionException;
import liquibase.ext.couchbase.executor.TransactionalStatementQueue;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;

import static com.couchbase.client.java.transactions.config.TransactionOptions.transactionOptions;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.TRANSACTION_TIMEOUT;

/**
 * Class for executing CRUD statements in transaction
 */
public class PlainTransactionExecutorService extends TransactionExecutorService {

    private static final TransactionOptions transactionOptions = transactionOptions().timeout(TRANSACTION_TIMEOUT.getCurrentValue());
    private final TransactionalStatementQueue transactionalStatementQueue = Scope.getCurrentScope()
            .getSingleton(TransactionalStatementQueue.class);

    public PlainTransactionExecutorService(Cluster cluster) {
        super(cluster);
    }

    @Override
    public void addStatementIntoQueue(CouchbaseTransactionStatement transactionStatement) {
        transactionalStatementQueue.add(transactionStatement.asTransactionAction(clusterOperator));
    }

    @Override
    public void executeStatementsInTransaction() {
        if (transactionalStatementQueue.isEmpty()) {
            return;
        }

        try {
            cluster.transactions()
                    .run(ctx -> transactionalStatementQueue.forEach(it -> it.accept(ctx)), transactionOptions);
        } catch (TransactionFailedException e) {
            throw new TransactionalStatementExecutionException(e);
        } finally {
            transactionalStatementQueue.clear();
        }
    }

    @Override
    public void clearStatementsQueue() {
        transactionalStatementQueue.clear();
    }
}
