package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.statement.CouchbaseStatement;

import static java.lang.String.format;

public class StatementExecutionException extends RuntimeException {

    private static final String msg = "An error was occured in statement %s execution";

    public StatementExecutionException(Class<? extends CouchbaseStatement> clz, Throwable e) {
        super(format(msg, clz.getName()), e);
    }

}
