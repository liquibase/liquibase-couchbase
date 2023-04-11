package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

/**
 * Exception thrown when bucket does not exist. Thrown by {@link liquibase.ext.couchbase.precondition.IndexExistsPrecondition}
 * @see PreconditionFailedException
 */

@Getter
public class IndexNotExistsPreconditionException extends PreconditionFailedException {

    private static final String template = "Index %s(bucket name - %s, primary - %s) does not exist";
    private final String message;

    public IndexNotExistsPreconditionException(String bucketName,
                                               String indexName,
                                               boolean isPrimary,
                                               DatabaseChangeLog changeLog,
                                               Precondition precondition) {
        super(format(template, indexName, bucketName, isPrimary), changeLog, precondition);
        message = format(template, indexName, bucketName, isPrimary);
    }
}
