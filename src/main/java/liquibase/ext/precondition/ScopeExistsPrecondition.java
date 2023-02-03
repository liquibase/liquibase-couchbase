package liquibase.ext.precondition;

import com.wdt.couchbase.exception.ScopeNotExistsPreconditionException;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.ScopeExistsStatement;
import lombok.Data;

@Data
public class ScopeExistsPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;
    private String scopeName;

    @Override
    public String getName() {
        return "scopeExists";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws ScopeNotExistsPreconditionException {
        final ScopeExistsStatement scopeExistsStatement = new ScopeExistsStatement(bucketName, scopeName);

        if (scopeExistsStatement.isScopeExists((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new ScopeNotExistsPreconditionException(scopeName, bucketName, changeLog, this);
    }
}
