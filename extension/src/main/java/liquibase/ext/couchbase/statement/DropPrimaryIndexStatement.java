package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;

import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropPrimaryIndexStatement extends CouchbaseStatement {
    private final Keyspace keyspace;

    @Override
    public void execute(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        cluster.queryIndexes().dropPrimaryIndex(bucket(connection, keyspace), options(keyspace));
    }

    private static String bucket(CouchbaseConnection connection, Keyspace keyspace) {
        return Optional.ofNullable(keyspace.getBucket())
                .filter(StringUtils::isNotBlank)
                .orElseGet(() -> connection.getDatabase().name());
    }

    private static DropPrimaryQueryIndexOptions options(Keyspace keyspace) {
        return DropPrimaryQueryIndexOptions
                .dropPrimaryQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
    }
}
