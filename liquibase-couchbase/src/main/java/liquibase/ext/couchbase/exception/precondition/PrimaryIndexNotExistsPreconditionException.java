package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

/**
 * Exception thrown when primary index does not exist. Thrown by {@link liquibase.ext.couchbase.precondition.PrimaryIndexExistsPrecondition}
 * @see PreconditionFailedException
 */

@Getter
public class PrimaryIndexNotExistsPreconditionException extends PreconditionFailedException {

    private static final String template = "Primary index %s(bucket name - %s, scope name - %s, collection - %s) does not exist";
    private final String message;

    public PrimaryIndexNotExistsPreconditionException(String bucketName,
                                               String indexName,
                                               String scopeName,
                                               String collectionName,
                                               DatabaseChangeLog changeLog,
                                               Precondition precondition) {
        super(format(template, indexName, bucketName, scopeName, collectionName), changeLog, precondition);
        message = format(template, indexName, bucketName, scopeName, collectionName);
    }
}
