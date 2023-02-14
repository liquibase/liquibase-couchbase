package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

@Getter
public class CollectionsNotExistsPreconditionException extends PreconditionFailedException {

    private static final String template = "Collection %s does not exist in bucket %s in scope %s";
    private final String message;

    public CollectionsNotExistsPreconditionException(String collectionName, String bucketName, String scopeName,
                                                     DatabaseChangeLog changeLog, Precondition precondition) {
        super(format(template, collectionName, bucketName, scopeName), changeLog, precondition);
        message = format(template, collectionName, bucketName, scopeName);
    }
}
