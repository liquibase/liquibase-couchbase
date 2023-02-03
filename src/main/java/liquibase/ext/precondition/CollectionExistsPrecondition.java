package liquibase.ext.precondition;

import com.wdt.couchbase.exception.CollectionsNotExistsPreconditionException;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.PreconditionFailedException;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.CollectionExistsStatement;
import lombok.Data;

@Data
public class CollectionExistsPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;
    private String scopeName;
    private String collectionName;

    @Override
    public String getName() {
        return "collectionExists";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws PreconditionFailedException {
        final CollectionExistsStatement collectionExistsStatement =
                new CollectionExistsStatement(bucketName, scopeName, collectionName);

        if (collectionExistsStatement.isCollectionExists((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new CollectionsNotExistsPreconditionException(collectionName, bucketName, scopeName, changeLog, this);
    }

    @Override
    public String getSerializedObjectNamespace() {
        return GENERIC_CHANGELOG_EXTENSION_NAMESPACE;
    }
}
