package liquibase.ext.couchbase.precondition;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.statement.BucketExistsStatement;
import liquibase.ext.couchbase.exception.BucketNotExistsPreconditionException;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import lombok.Data;

import static java.lang.String.format;

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

        if (bucketExistsStatement.isBucketExists((CouchbaseConnection) database.getConnection())) {
            return;
        }
        throw new BucketNotExistsPreconditionException(bucketName, changeLog, this);
    }

}
