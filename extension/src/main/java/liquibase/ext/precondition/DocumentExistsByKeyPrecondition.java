package liquibase.ext.precondition;

import com.wdt.couchbase.exception.DocumentNotExistsPreconditionException;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.PreconditionFailedException;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.DocumentExistsByKeyStatement;
import lombok.Data;

@Data
public class DocumentExistsByKeyPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;
    private String scopeName;
    private String collectionName;
    private String key;

    @Override
    public String getName() {
        return "documentExists";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws PreconditionFailedException {
        final DocumentExistsByKeyStatement documentExistsByKeyStatement =
                new DocumentExistsByKeyStatement(bucketName, scopeName, collectionName, key);

        if (documentExistsByKeyStatement.isCDocumentExists((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new DocumentNotExistsPreconditionException(key, bucketName, scopeName, collectionName, changeLog, this);
    }

}
