package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

/**
 * An exception thrown when document does not exist. Thrown by {@link liquibase.ext.couchbase.precondition.DocumentExistsByKeyPrecondition}
 * @see PreconditionFailedException
 */

@Getter
public class DocumentNotExistsPreconditionException extends PreconditionFailedException {

    private static final String template = "Key %s does not exist in bucket %s in scope %s and collection %s";
    private final String message;

    public DocumentNotExistsPreconditionException(String key, String bucketName, String scopeName, String collectionName,
                                                  DatabaseChangeLog changeLog, Precondition precondition) {
        super(format(template, key, bucketName, scopeName, collectionName), changeLog, precondition);
        message = format(template, key, bucketName, scopeName, collectionName);
    }
}
