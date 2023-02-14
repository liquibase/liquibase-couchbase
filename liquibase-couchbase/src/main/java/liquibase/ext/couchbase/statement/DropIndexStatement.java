package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * A statement to drop secondary index for a keyspace
 *
 * @see CouchbaseStatement
 * @see DropQueryIndexOptions
 * @see Keyspace
 *
 */

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
