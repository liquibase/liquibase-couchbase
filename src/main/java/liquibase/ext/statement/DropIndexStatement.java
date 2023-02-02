package liquibase.ext.statement;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import liquibase.ext.database.CouchbaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropIndexStatement extends CouchbaseStatement {
    private final String indexName;
    private final String bucketName;
    private final String collectionName;
    private final String scopeName;

    @Override
    public void execute(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        String bucket = Optional.ofNullable(bucketName)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> connection.getDatabase().name());

        if (isNotBlank(collectionName) && isNotBlank(scopeName)) {
            DropQueryIndexOptions options = DropQueryIndexOptions.dropQueryIndexOptions()
                    .collectionName(collectionName)
                    .scopeName(scopeName);
            cluster.queryIndexes().dropIndex(bucket, indexName, options);
        } else {
            cluster.queryIndexes().dropIndex(bucket, indexName);
        }
    }
}
