package liquibase.ext.couchbase.exception;

import static java.lang.String.format;

public class BucketExistsException extends RuntimeException {

    private static final String template = "Bucket with name [%s] already exists";

    public BucketExistsException(String bucketName) {
        super(format(template, bucketName));
    }
}
