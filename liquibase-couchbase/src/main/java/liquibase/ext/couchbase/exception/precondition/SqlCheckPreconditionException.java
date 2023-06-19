package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.ext.couchbase.precondition.QueryCustomCheckPrecondition;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

/**
 * Exception thrown when bucket does not exist. Thrown by {@link QueryCustomCheckPrecondition}
 * @see PreconditionFailedException
 */

@Getter
public class SqlCheckPreconditionException extends PreconditionFailedException {

    private static final String template = "Result of [%s] query is differ then expected[%s]";
    private final String message;

    public SqlCheckPreconditionException(String query, String expectedResult, DatabaseChangeLog changeLog, Precondition precondition) {
        super(format(template, query, expectedResult), changeLog, precondition);
        message = format(template, query, expectedResult);
    }
}
