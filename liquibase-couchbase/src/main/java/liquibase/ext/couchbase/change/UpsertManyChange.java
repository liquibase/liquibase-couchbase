package liquibase.ext.couchbase.change;


import com.wdt.couchbase.Keyspace;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.UpsertManyStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import static com.wdt.couchbase.Keyspace.keyspace;

@Getter
@Setter
@DatabaseChange(
        name = "upsertMany",
        description = "Upserts multiple documents into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class UpsertManyChange extends CouchbaseChange {

    private String bucketName;
    private String id;
    private String scopeName;
    private String collectionName;
    private List<Document> documents;

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents upserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[]{new UpsertManyStatement(keyspace, documents)
        };
    }
}

