package liquibase.ext.couchbase.executor;

import java.util.List;
import java.util.Map;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.executor.AbstractExecutor;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
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
import static java.util.Collections.emptyList;

/**
 * Low-level executor for {@link CouchbaseStatement}
 * Currently, not supporting part of liquibase functionality cause of NoSQL nature of Couchbase.<br><br>
 * Ensures that executed statements extend {@link CouchbaseStatement}.
 *
 * @see AbstractExecutor
 * @see LiquibaseService
 * @see CouchbaseStatement
 */

@LiquibaseService
@NoArgsConstructor
public class NoSqlExecutor extends AbstractExecutor {

    public static final String EXECUTOR_NAME = "jdbc";
    private final Logger log = Scope.getCurrentScope().getLog(getClass());
    private final TransactionalStatementQueue transactionalStatementQueue =
            Scope.getCurrentScope().getSingleton(TransactionalStatementQueue.class);

    @Override
    public void setDatabase(final Database database) {
        super.setDatabase(database);
    }

    @Override
    public <T> T queryForObject(SqlStatement sql, Class<T> requiredType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T queryForObject(SqlStatement sql, Class<T> requiredType, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long queryForLong(SqlStatement sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long queryForLong(SqlStatement sql, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int queryForInt(SqlStatement sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int queryForInt(SqlStatement sql, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List queryForList(SqlStatement sql, Class elementType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List queryForList(SqlStatement sql, Class elementType, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<String, ?>> queryForList(SqlStatement sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<Map<String, ?>> queryForList(SqlStatement sql, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @SuppressWarnings("unchecked")
    private <T extends CouchbaseLiquibaseDatabase> T getDatabase() {
        return (T) database;
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
    public void execute(final SqlStatement sql) throws DatabaseException {
        this.execute(sql, emptyList());
    }

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

        throw new IllegalArgumentException("Couchbase cannot execute " + sql.getClass().getName() + " statements");
    }

    private void doExecute(CouchbaseStatement sql) throws DatabaseException {
        try {
            ClusterOperator clusterOperator = new ClusterOperator(getDatabase().getConnection().getCluster());
            sql.execute(clusterOperator);
        } catch (final Exception e) {
            throw new DatabaseException("Could not execute", e);
        }
    }

    private CouchbaseTransactionAction buildTransactionalAction(CouchbaseTransactionStatement sql) throws DatabaseException {
        try {
            ClusterOperator clusterOperator = new ClusterOperator(getDatabase().getConnection().getCluster());
            return sql.asTransactionAction(clusterOperator);
        } catch (final Exception e) {
            throw new DatabaseException("Could not execute", e);
        }
    }

    @Override
    public int update(SqlStatement sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(SqlStatement sql, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void comment(final String message) {
        log.info(message);
    }

    @Override
    public boolean updatesDatabase() {
        return true;
    }
}
