package liquibase.ext.couchbase.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.exception.PreconditionFailedException;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.CollectionNotExistsPreconditionException;
import liquibase.ext.couchbase.statement.CollectionExistsStatement;
import lombok.Data;

/**
 *
 * A precondition that checks if a collection exists.
 *
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see CollectionNotExistsPreconditionException
 *
 */

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

        if (collectionExistsStatement.isTrue((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new CollectionNotExistsPreconditionException(collectionName, bucketName, scopeName, changeLog, this);
    }

    @Override
    public String getSerializedObjectNamespace() {
        return GENERIC_CHANGELOG_EXTENSION_NAMESPACE;
    }
}
