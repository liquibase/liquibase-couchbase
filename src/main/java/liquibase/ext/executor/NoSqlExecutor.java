package liquibase.ext.executor;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.executor.AbstractExecutor;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.statement.CouchbaseStatement;
import liquibase.logging.Logger;
import liquibase.servicelocator.LiquibaseService;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

@LiquibaseService
@NoArgsConstructor
public class NoSqlExecutor extends AbstractExecutor {

    public static final String EXECUTOR_NAME = "jdbc";
    private final Logger log = Scope.getCurrentScope().getLog(getClass());

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
    public void execute(final SqlStatement sql, final List<SqlVisitor> sqlVisitors) throws DatabaseException {
        if (sql instanceof CouchbaseStatement) {
            doExecute((CouchbaseStatement) sql);
            return;
        }

        throw new IllegalArgumentException("Couchbase cannot execute " + sql.getClass().getName() + " statements");
    }

    private void doExecute(CouchbaseStatement sql) throws DatabaseException {
        try {
            sql.execute(getDatabase().getConnection());
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
