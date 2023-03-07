package liquibase.ext.couchbase.exception;

import lombok.NonNull;

import static java.lang.String.format;

public class CollectionNotExistsException extends RuntimeException {

    private static final String template = "Collection [%s] does not exist in scope [%s]";

    public CollectionNotExistsException(@NonNull String collectionName,
                                        @NonNull String scopeName) {
        super(format(template, collectionName, scopeName));
    }
}
