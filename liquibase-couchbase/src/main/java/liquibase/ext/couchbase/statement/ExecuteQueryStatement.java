package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A statement to execute sql++/n1ql query
 * @see CouchbaseStatement
 * @see Keyspace
 */

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class ExecuteQueryStatement extends CouchbaseStatement {
    private final String query;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        clusterOperator.getCluster().query(query);
    }
}
