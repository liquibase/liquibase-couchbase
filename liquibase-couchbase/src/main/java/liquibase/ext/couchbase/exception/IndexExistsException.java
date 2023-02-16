package liquibase.ext.couchbase.exception;

import lombok.NonNull;

import static java.lang.String.format;

public class IndexExistsException extends RuntimeException {

    private static final String template = "Index [%s] exists";

    public IndexExistsException(@NonNull String indexName) {
        super(format(template, indexName));
    }
}
