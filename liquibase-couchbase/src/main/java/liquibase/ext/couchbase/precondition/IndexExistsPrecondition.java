package liquibase.ext.couchbase.precondition;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.IndexNotExistsPreconditionException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private Boolean isPrimary;

    @Override
    public String getName() {
        return "doesIndexExist"; // liquibase has indexExists flag
    }

    @Override
    public void executeAndCheckStatement(Database database, DatabaseChangeLog changeLog) throws IndexNotExistsPreconditionException {
        ClusterOperator operator = new ClusterOperator(((CouchbaseConnection) database.getConnection()).getCluster());

        if (operator.indexExists(indexName, bucketName, scopeName, isPrimary)) {
            return;
        }

        throw new IndexNotExistsPreconditionException(bucketName, indexName, scopeName, isPrimary, changeLog, this);
    }

}
