package liquibase.ext.couchbase.statement;

import static java.lang.String.format;

import liquibase.Scope;
import liquibase.ext.couchbase.exception.BucketNotExistException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.logging.Logger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A statement to drop bucket
 *
 * @see CouchbaseStatement
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropBucketStatement extends CouchbaseStatement {

    private static final String BUCKET_NOT_EXISTS_MESSAGE = "Bucket %s does not exist, skip removing";
    private final Logger logger = Scope.getCurrentScope().getLog(DropBucketStatement.class);

    private final String bucketName;

    private final Boolean ignoreIfNotExists;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        boolean bucketNotExist = !clusterOperator.isBucketExists(bucketName);

        if (ignoreIfNotExists && bucketNotExist) {
            logger.info(format(BUCKET_NOT_EXISTS_MESSAGE, bucketName));
            return;
        }

        if (bucketNotExist) {
            throw new BucketNotExistException(bucketName);
        }

        clusterOperator.dropBucket(bucketName);
    }

}
