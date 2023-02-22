package liquibase.ext.couchbase.precondition;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.statement.BucketExistsStatement;
import liquibase.ext.couchbase.exception.precondition.BucketNotExistsPreconditionException;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import lombok.Data;

/**
 *
 * A precondition that checks if a bucket exists.
 *
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see BucketNotExistsPreconditionException
 *
 */

@Data
public class BucketExistsPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;

    @Override
    public String getName() {
        return "bucketExists";
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws BucketNotExistsPreconditionException {
        final BucketExistsStatement bucketExistsStatement = new BucketExistsStatement(bucketName);

        if (bucketExistsStatement.isTrue((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new BucketNotExistsPreconditionException(bucketName, changeLog, this);
    }

}
