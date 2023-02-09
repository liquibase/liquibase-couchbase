package liquibase.ext.couchbase.statement;


import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertOneStatement extends CouchbaseStatement {

    private final String bucketName;
    private final String id;
    private final String document;
    private final String scopeName;
    private final String collectionName;

    @Override
    public void execute(CouchbaseConnection connection) {
        InsertManyStatement statement = new InsertManyStatement(bucketName,
                Collections.singletonMap(id, document), scopeName, collectionName);
        statement.execute(connection);
    }
}

