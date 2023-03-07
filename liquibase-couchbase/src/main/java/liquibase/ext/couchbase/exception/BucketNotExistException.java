package liquibase.ext.couchbase.exception;

import lombok.NonNull;

import static java.lang.String.format;

public class BucketNotExistException extends RuntimeException {

    private static final String template = "Bucket [%s] not exists";

    public BucketNotExistException(@NonNull String bucketName) {
        super(format(template, bucketName));
    }

}