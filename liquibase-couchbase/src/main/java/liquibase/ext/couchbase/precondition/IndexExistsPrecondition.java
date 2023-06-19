package liquibase.ext.couchbase.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.IndexNotExistsPreconditionException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.BucketScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

/**
 * A precondition that checks if the index exists.
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see IndexNotExistsPreconditionException
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndexExistsPrecondition extends AbstractCouchbasePrecondition {

    private String bucketName;
    private String indexName;
    private String scopeName;

    private String collectionName;

    @Override
    public String getName() {
        return "doesIndexExist"; // liquibase has indexExists flag
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws IndexNotExistsPreconditionException {
        ClusterOperator operator = new ClusterOperator(((CouchbaseConnection) database.getConnection()).getCluster());

        if (!doesIndexExist(operator)) {
            throw new IndexNotExistsPreconditionException(bucketName, indexName, scopeName, collectionName, changeLog, this);
        }
    }

    private boolean doesIndexExist(ClusterOperator operator) {
        if (isBlank(scopeName) && isBlank(collectionName)) {
            return operator.indexExists(indexName, bucketName);
        }

        if (isNoneBlank(scopeName) && isBlank(collectionName)) {
            BucketScope bucketScope = BucketScope.bucketScope(bucketName, scopeName);
            return operator.indexExists(indexName, bucketScope);
        }

        return operator.getBucketOperator(bucketName)
                .getCollectionOperator(collectionName, scopeName)
                .collectionIndexExists(indexName);
    }

}
