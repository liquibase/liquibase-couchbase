package liquibase.ext.sqlgenerator;

import liquibase.database.Database;
import liquibase.exception.ValidationErrors;
import liquibase.ext.statement.CouchbaseStatement;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AbstractSqlGenerator;

public class NoSqlGenerator extends AbstractSqlGenerator<CouchbaseStatement> {

    @Override
    public ValidationErrors validate(CouchbaseStatement statement, Database database,
                                     SqlGeneratorChain<CouchbaseStatement> sqlGeneratorChain) {
        return null;
    }

    @Override
    public Sql[] generateSql(CouchbaseStatement statement, Database database, SqlGeneratorChain<CouchbaseStatement> sqlGeneratorChain) {
        return new Sql[0];
    }

    @Override
    public boolean generateStatementsIsVolatile(Database database) {
        return true;
    }
}
