package liquibase.ext.precondition;

import com.wdt.couchbase.exception.BucketNotExistsPreconditionException;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.visitor.ChangeExecListener;
import liquibase.database.Database;
import liquibase.exception.PreconditionErrorException;
import liquibase.exception.PreconditionFailedException;
import liquibase.exception.ValidationErrors;
import liquibase.exception.Warnings;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.BucketExistsStatement;
import liquibase.precondition.AbstractPrecondition;
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
