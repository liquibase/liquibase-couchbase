package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.exception.BucketNotExistException;
import liquibase.ext.couchbase.operator.ClusterOperator;
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

    private final String bucketName;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        if (!clusterOperator.isBucketExists(bucketName)) {
            throw new BucketNotExistException(bucketName);
        }

        clusterOperator.dropBucket(bucketName);
    }

}
