package liquibase.ext.couchbase.precondition;

import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.DocumentNotExistsPreconditionException;
import liquibase.ext.couchbase.statement.DocumentExistsByKeyStatement;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.PreconditionFailedException;
import lombok.Data;

import static com.wdt.couchbase.Keyspace.keyspace;

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
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        final DocumentExistsByKeyStatement documentExistsByKeyStatement = new DocumentExistsByKeyStatement(keyspace, key);

        if (documentExistsByKeyStatement.isDocumentExists((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new DocumentNotExistsPreconditionException(key, bucketName, scopeName, collectionName, changeLog, this);
    }

}
