package liquibase.ext.couchbase.exception.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.exception.PreconditionFailedException;
import liquibase.ext.couchbase.precondition.QueryCountCheckPrecondition;
import liquibase.precondition.Precondition;
import lombok.Getter;

import static java.lang.String.format;

/**
 * An exception thrown when scope does not exist. Thrown by {@link QueryCountCheckPrecondition}
 * @see PreconditionFailedException
 */

@Getter
public class SqlCheckCountPreconditionException extends PreconditionFailedException {

    private static final String template = "Sql precondition query [%s] result is different then expected count [%d]";
    private final String message;

    public SqlCheckCountPreconditionException(String query, Integer count,
                                              DatabaseChangeLog changeLog, Precondition precondition) {
        super(format(template, query, count), changeLog, precondition);
        message = format(template, query, count);
    }
}
