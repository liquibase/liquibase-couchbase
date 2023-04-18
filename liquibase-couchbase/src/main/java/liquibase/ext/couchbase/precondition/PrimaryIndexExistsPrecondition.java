package liquibase.ext.couchbase.precondition;

import com.couchbase.client.java.Collection;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.IndexNotExistsPreconditionException;
import liquibase.ext.couchbase.exception.precondition.PrimaryIndexNotExistsPreconditionException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.BucketScope;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;

/**
 * A precondition that checks if the primary index exists.
 * @see AbstractCouchbasePrecondition
 * @see liquibase.precondition.AbstractPrecondition
 * @see IndexNotExistsPreconditionException
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PrimaryIndexExistsPrecondition extends AbstractCouchbasePrecondition {

    private String indexName;
    private String bucketName;

    private String scopeName;

    private String collectionName;

    @Override
    public String getName() {
        return "doesPrimaryIndexExist"; // liquibase has indexExists flag
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws PrimaryIndexNotExistsPreconditionException {
        ClusterOperator operator = new ClusterOperator(((CouchbaseConnection) database.getConnection()).getCluster());

        if (!doesIndexExist(operator)) {
            throw new PrimaryIndexNotExistsPreconditionException(bucketName, indexName, scopeName, collectionName, changeLog, this);
        }
    }

    private boolean doesIndexExist(ClusterOperator operator) {
        if (isBlank(scopeName) && isBlank(collectionName)) {
            return operator.primaryIndexExists(indexName, bucketName);
        }

        if (isNoneBlank(scopeName) && isBlank(collectionName)) {
            BucketScope bucketScope = BucketScope.bucketScope(bucketName, scopeName);
            return operator.primaryIndexExists(indexName, bucketScope);
        }

        Collection collection = operator.getBucketOperator(bucketName).getCollection(collectionName, scopeName);

        return operator.getCollectionOperator(collection).collectionPrimaryIndexExists(indexName);
    }

}
