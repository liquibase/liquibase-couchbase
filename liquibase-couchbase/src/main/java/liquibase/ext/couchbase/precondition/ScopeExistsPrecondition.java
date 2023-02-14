package liquibase.ext.couchbase.precondition;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.statement.ScopeExistsStatement;
import liquibase.ext.couchbase.exception.precondition.ScopeNotExistsPreconditionException;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
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
