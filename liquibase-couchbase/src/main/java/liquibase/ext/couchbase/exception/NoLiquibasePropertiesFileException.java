package liquibase.ext.couchbase.exception;

import liquibase.ext.couchbase.statement.CouchbaseStatement;

import static java.lang.String.format;

public class NoLiquibasePropertiesFileException extends RuntimeException {

    private static final String message = "liquibase.properties file not found in resources folder";

    public NoLiquibasePropertiesFileException() {
        super(message);
    }

}
