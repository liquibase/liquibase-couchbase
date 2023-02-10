package liquibase.ext.couchbase.statement;


import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static java.util.Collections.singletonMap;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertOneStatement extends CouchbaseStatement {

    private final Keyspace keyspace;
    private final String id;
    private final String document;

    @Override
    public void execute(CouchbaseConnection connection) {
        UpsertManyStatement statement = new UpsertManyStatement(keyspace, singletonMap(id, document));
        statement.execute(connection);
    }
}

