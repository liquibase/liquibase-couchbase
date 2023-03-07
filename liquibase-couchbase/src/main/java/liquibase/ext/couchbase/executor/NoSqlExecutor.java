package liquibase.ext.couchbase.executor;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.executor.AbstractExecutor;
import liquibase.sql.visitor.SqlVisitor;
import liquibase.statement.SqlStatement;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

/**
 * Contains stubs for unsupported operations
 */
public abstract class NoSqlExecutor extends AbstractExecutor {

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
    public int update(SqlStatement sql) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(SqlStatement sql, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean updatesDatabase() {
        return true;
    }

    @Override
    public void execute(final SqlStatement sql) throws DatabaseException {
        this.execute(sql, emptyList());
    }

    @Override
    public List<Map<String, ?>> queryForList(SqlStatement sql, List<SqlVisitor> sqlVisitors) {
        throw new UnsupportedOperationException();
    }
}
