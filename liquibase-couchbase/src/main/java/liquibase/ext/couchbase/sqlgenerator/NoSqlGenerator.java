package liquibase.ext.couchbase.sqlgenerator;

import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.ext.couchbase.statement.NoSqlStatement;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;

/**
 *
 * Couchbase does not support SQL statements. This class is used to generate empty SQL statements.
 *
 */

public class NoSqlGenerator extends AbstractSqlGenerator<NoSqlStatement> {

    @Override
    public ValidationErrors validate(NoSqlStatement statement, Database database,
                                     SqlGeneratorChain<NoSqlStatement> sqlGeneratorChain) {
        return null;
    }

    @Override
    public Sql[] generateSql(NoSqlStatement statement, Database database, SqlGeneratorChain<NoSqlStatement> sqlGeneratorChain) {
        return new Sql[0];
    }

    @Override
    public boolean generateStatementsIsVolatile(Database database) {
        return true;
    }
}
