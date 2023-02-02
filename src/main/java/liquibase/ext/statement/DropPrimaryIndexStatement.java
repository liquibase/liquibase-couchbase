package liquibase.ext.statement;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import liquibase.ext.database.CouchbaseConnection;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Data
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropPrimaryIndexStatement extends CouchbaseStatement {
    private final String bucketName;
    private final String collectionName;
    private final String scopeName;

    @Override
    public void execute(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        String bucket = Optional.ofNullable(bucketName)
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> connection.getDatabase().name());

        if (StringUtils.isNotBlank(collectionName) && StringUtils.isNotBlank(scopeName)) {
            DropPrimaryQueryIndexOptions options = DropPrimaryQueryIndexOptions
                    .dropPrimaryQueryIndexOptions()
                    .collectionName(collectionName)
                    .scopeName(scopeName);
            cluster.queryIndexes().dropPrimaryIndex(bucket, options);
        } else {
            cluster.queryIndexes().dropPrimaryIndex(bucket);
        }
    }
}
