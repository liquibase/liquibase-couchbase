package liquibase.ext.couchbase.exception;

import lombok.NonNull;

import static java.lang.String.format;

public class IndexNotExistsException extends RuntimeException {

    private static final String template = "Index [%s] not exists";

    public IndexNotExistsException(@NonNull String indexName) {
        super(format(template, indexName));
    }
}
