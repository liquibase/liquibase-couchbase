package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * A statement to drop primary index for a keyspace
 *
 * @see CouchbaseStatement
 * @see DropPrimaryQueryIndexOptions
 * @see Keyspace
 *
 */

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
