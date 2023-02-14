package liquibase.ext.couchbase.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.PreconditionFailedException;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.CollectionsNotExistsPreconditionException;
import liquibase.ext.couchbase.statement.CollectionExistsStatement;
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
