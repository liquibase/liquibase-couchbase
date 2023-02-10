package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropPrimaryIndexStatement extends CouchbaseStatement {
    private final Keyspace keyspace;

    @Override
    public void execute(CouchbaseConnection connection) {
        connection.getCluster()
                .queryIndexes()
                .dropPrimaryIndex(keyspace.getBucket(), options(keyspace));
    }

    private static DropPrimaryQueryIndexOptions options(Keyspace keyspace) {
        return DropPrimaryQueryIndexOptions
                .dropPrimaryQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
    }
}
