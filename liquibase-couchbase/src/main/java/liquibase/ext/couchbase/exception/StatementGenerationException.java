package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.change.CouchbaseChange;

import static java.lang.String.format;

public class StatementGenerationException extends RuntimeException {

    private static final String msg = "An error has occurred during statement generation from %s, cause %s, please check your parameters";

    public StatementGenerationException(Class<? extends CouchbaseChange> clz, Throwable t) {
        super(format(msg, clz.getName(), t.toString()), t);
    }

}
