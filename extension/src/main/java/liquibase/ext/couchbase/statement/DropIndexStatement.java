package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropIndexStatement extends CouchbaseStatement {
    private final String indexName;
    private final Keyspace keyspace;

    @Override
    public void execute(CouchbaseConnection connection) {
        connection.getCluster()
                .queryIndexes()
                .dropIndex(keyspace.getBucket(), indexName, options(keyspace));
    }

    private static DropQueryIndexOptions options(Keyspace keyspace) {
        return DropQueryIndexOptions.dropQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
    }
}
