package liquibase.ext.couchbase.statement;


import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertOneStatement extends CouchbaseStatement {
    private final Keyspace keyspace;
    private final String id;
    private final String document;

    @Override
    public void execute(CouchbaseConnection connection) {
        InsertManyStatement statement = new InsertManyStatement(keyspace, Collections.singletonList(new Document(id, document)));
        statement.execute(connection);
    }
}

