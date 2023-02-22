package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.change.CouchbaseChange;

import static java.lang.String.format;

public class StatementGenerationException extends RuntimeException {

    private static final String msg = "An error was occured during generation statement from %s , cause %s ,please check your parameters";

    public StatementGenerationException(Class<? extends CouchbaseChange> clz, Throwable t) {
        super(format(msg, clz.getName(), t.toString()), t);
    }

}
