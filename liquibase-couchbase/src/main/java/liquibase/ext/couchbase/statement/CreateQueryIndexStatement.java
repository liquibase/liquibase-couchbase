package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Field;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CreateQueryIndexStatement extends CouchbaseStatement {
    private final String bucketName;
    private final String indexName;
    private final List<Field> fields;
    private final CreateQueryIndexOptions options;

    @Override
    public void execute(ClusterOperator clusterOperator) {
        clusterOperator.createQueryIndex(indexName, bucketName, fields, options);
    }

    @Override
    public void execute(CouchbaseConnection connection) {
        execute(new ClusterOperator(connection.getCluster()));
    }
}