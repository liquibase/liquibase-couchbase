package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

@Getter
public class BucketNotExistsPreconditionException extends PreconditionFailedException {

    private static final String template = "Bucket %s does not exist";
    private final String message;

    public BucketNotExistsPreconditionException(String bucketName, DatabaseChangeLog changeLog, Precondition precondition) {
        super(format(template, bucketName), changeLog, precondition);
        message = format(template, bucketName);
    }
}
