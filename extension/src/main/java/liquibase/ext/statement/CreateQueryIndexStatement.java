package liquibase.ext.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.ext.database.CouchbaseConnection;
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
    private final List<String> fields;
    private final CreateQueryIndexOptions options;

    @Override
    public void execute(CouchbaseConnection connection) {
        connection.getCluster().queryIndexes().createIndex(bucketName, indexName, fields, options);
    }
}