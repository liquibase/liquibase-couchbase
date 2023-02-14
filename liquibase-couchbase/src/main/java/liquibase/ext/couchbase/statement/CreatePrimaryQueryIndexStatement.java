package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CreatePrimaryQueryIndexStatement extends CouchbaseStatement {
    private final String bucketName;
    private final CreatePrimaryQueryIndexOptions options;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        clusterOperator.createPrimaryIndex(bucketName, options);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        execute(new ClusterOperator(connection.getCluster()));
    }
}
