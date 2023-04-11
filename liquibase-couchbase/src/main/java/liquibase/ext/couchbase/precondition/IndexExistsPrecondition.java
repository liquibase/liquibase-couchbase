package liquibase.ext.couchbase.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.IndexNotExistsPreconditionException;
import liquibase.ext.couchbase.statement.IndexExistsStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A precondition that checks if a bucket exists.
 *
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see IndexNotExistsPreconditionException
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexExistsPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;
    private String indexName;
    private boolean isPrimary;

    @Override
    public String getName() {
        return "indexExists";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws IndexNotExistsPreconditionException {
        final IndexExistsStatement indexExistsStatement = new IndexExistsStatement(bucketName, indexName, isPrimary);

        if (indexExistsStatement.isTrue((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new IndexNotExistsPreconditionException(bucketName, indexName, isPrimary, changeLog, this);
    }

}
