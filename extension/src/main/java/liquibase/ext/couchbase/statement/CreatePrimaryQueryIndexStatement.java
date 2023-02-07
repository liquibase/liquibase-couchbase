package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;

import liquibase.ext.couchbase.database.CouchbaseConnection;
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
    public void execute(CouchbaseConnection connection) {
        connection.getCluster().queryIndexes().createPrimaryIndex(bucketName, options);
    }
}
