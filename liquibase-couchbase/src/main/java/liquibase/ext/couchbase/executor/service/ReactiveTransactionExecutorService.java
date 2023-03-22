package liquibase.ext.couchbase.executor.service;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.transactions.config.TransactionOptions;
import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.TransactionalReactiveStatementExecutionException;
import liquibase.ext.couchbase.executor.TransactionalReactiveStatementQueue;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import liquibase.ext.couchbase.types.CouchbaseReactiveTransactionAction;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import static com.couchbase.client.java.transactions.config.TransactionOptions.transactionOptions;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.REACTIVE_TRANSACTION_PARALLEL_THREADS;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.TRANSACTION_TIMEOUT;

/**
 * Class for executing CRUD statements in reactive transaction
 */
public class ReactiveTransactionExecutorService extends TransactionExecutorService {

    private static final TransactionOptions transactionOptions = transactionOptions().timeout(TRANSACTION_TIMEOUT.getCurrentValue());
    private final TransactionalReactiveStatementQueue transactionalReactiveStatementQueue = Scope.getCurrentScope()
            .getSingleton(TransactionalReactiveStatementQueue.class);

    public ReactiveTransactionExecutorService(Cluster cluster) {
        super(cluster);
    }

    @Override
    public void addStatementIntoQueue(CouchbaseTransactionStatement transactionStatement) {
        transactionalReactiveStatementQueue.add(transactionStatement.asTransactionReactiveAction(clusterOperator));
    }

    @Override
    public void executeStatementsInTransaction() {
        if (transactionalReactiveStatementQueue.isEmpty()) {
            return;
        }

        try {
            executeStatements();
        } catch (TransactionFailedException e) {
            throw new TransactionalReactiveStatementExecutionException(e);
        } finally {
            transactionalReactiveStatementQueue.clear();
        }
    }

    private void executeStatements() {
        Flux<CouchbaseReactiveTransactionAction> statements = Flux.fromIterable(transactionalReactiveStatementQueue);
        cluster.reactive().transactions()
                .run(ctx -> statements
                        .parallel(REACTIVE_TRANSACTION_PARALLEL_THREADS.getCurrentValue())
                        .runOn(Schedulers.boundedElastic())
                        .concatMap(action -> action.apply(ctx))
                        .sequential()
                        .then(), transactionOptions)
                .block();
    }

    @Override
    public void clearStatementsQueue() {
        transactionalReactiveStatementQueue.clear();
    }
}
