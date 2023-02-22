package liquibase.ext.couchbase.executor;

import com.couchbase.client.java.transactions.error.TransactionFailedException;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.executor.AbstractExecutor;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.exception.StatementExecutionException;
import liquibase.ext.couchbase.exception.TransactionalStatementExecutionException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.CouchbaseStatement;
import liquibase.ext.couchbase.statement.CouchbaseTransactionStatement;
import liquibase.ext.couchbase.types.CouchbaseTransactionAction;
import liquibase.logging.Logger;
import liquibase.servicelocator.LiquibaseService;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.List;

/**
 * Low-level executor for {@link CouchbaseStatement} Currently, not supporting part of liquibase functionality cause of NoSQL nature of
 * Couchbase.<br><br> Ensures that executed statements extend {@link CouchbaseStatement}.
 * @see AbstractExecutor
 * @see LiquibaseService
 * @see CouchbaseStatement
 */

@LiquibaseService
@NoArgsConstructor
public class CouchbaseExecutor extends NoSqlExecutor {

    public static final String EXECUTOR_NAME = "jdbc";
    private final Logger log = Scope.getCurrentScope()
        .getLog(getClass());
    private final TransactionalStatementQueue transactionalStatementQueue = Scope.getCurrentScope()
        .getSingleton(TransactionalStatementQueue.class);


    @Override
    @SneakyThrows
    public void execute(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        if (sql instanceof CouchbaseStatement) {
            doExecute((CouchbaseStatement) sql);
            return;
        }

        if (sql instanceof CouchbaseTransactionStatement) {
            CouchbaseTransactionAction statement = buildTransactionalAction((CouchbaseTransactionStatement) sql);
            transactionalStatementQueue.add(statement);
            return;
        }

        throw new IllegalArgumentException("Couchbase cannot execute " + sql.getClass()
            .getName() + " statements");
    }

    private void doExecute(CouchbaseStatement sql) {
        try {
            ClusterOperator clusterOperator = new ClusterOperator(getDatabase().getConnection()
                .getCluster());
            sql.execute(clusterOperator);
        } catch (final Exception e) {
            throw new StatementExecutionException(sql.getClass(), e);
        }
    }

    private CouchbaseTransactionAction buildTransactionalAction(CouchbaseTransactionStatement sql) {
        try {
            ClusterOperator clusterOperator = new ClusterOperator(getDatabase().getConnection()
                .getCluster());
            return sql.asTransactionAction(clusterOperator);
        } catch (final TransactionFailedException e) {
            throw new TransactionalStatementExecutionException(sql.getClass(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private CouchbaseLiquibaseDatabase getDatabase() {
        return (CouchbaseLiquibaseDatabase) database;
    }

    @Override
    public String getName() {
        return EXECUTOR_NAME;
    }

    @Override
    public int getPriority() {
        return PRIORITY_SPECIALIZED;
    }

    @Override
    public boolean supports(Database database) {
        return database instanceof CouchbaseLiquibaseDatabase;
    }

    @Override
    public void comment(final String message) {
        log.info(message);
    }

}
