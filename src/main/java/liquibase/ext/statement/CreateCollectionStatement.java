package liquibase.ext.statement;

import liquibase.ext.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateCollectionStatement extends CouchbaseStatement {

    private final String collectionName;

    @Override
    public void execute(CouchbaseConnection connection) {
        //TODO implement
    }
}
