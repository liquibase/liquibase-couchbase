package liquibase.ext.couchbase.exception;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

@Getter
public class ScopeNotExistsPreconditionException extends PreconditionFailedException {

    private static final String template = "Scope %s does not exist in bucket %s";
    private final String message;

    public ScopeNotExistsPreconditionException(String scopeName, String bucketName,
                                               DatabaseChangeLog changeLog, Precondition precondition) {
        super(format(template, scopeName, bucketName), changeLog, precondition);
        message = format(template, scopeName, bucketName);
    }
}
