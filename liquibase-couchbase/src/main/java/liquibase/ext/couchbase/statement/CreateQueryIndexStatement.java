package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;

import java.util.Collection;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class CreateQueryIndexStatement extends CouchbaseStatement {
    private final String bucketName;
    private final String indexName;
    private final Collection<String> fields;
    private final CreateQueryIndexOptions options;

    @Override
    public void execute(CouchbaseConnection connection) {
        connection.getCluster().queryIndexes().createIndex(bucketName, indexName, fields, options);
    }
}