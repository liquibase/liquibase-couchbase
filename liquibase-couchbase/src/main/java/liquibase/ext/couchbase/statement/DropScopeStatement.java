package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * A statement to drop scope
 *
 * @see CouchbaseStatement
 * @see Keyspace
 *
 */

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropScopeStatement extends CouchbaseStatement {
    private final String scopeName;
    private final String bucketName;

    @Override
    public void execute(ClusterOperator clusterOperator){
        clusterOperator.getBucketOperator(bucketName).dropScope(scopeName);
    }
}
