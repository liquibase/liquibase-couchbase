package liquibase.ext.couchbase.change;


import com.wdt.couchbase.Keyspace;

import java.util.Map;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.UpsertOneStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static com.wdt.couchbase.Keyspace.keyspace;
import static liquibase.ext.couchbase.types.Document.document;

@Getter
@Setter
@DatabaseChange(
        name = "upsertOne",
        description = "Upserts a document into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class UpsertOneChange extends CouchbaseChange {

    private String bucketName;
    private String id;
    private String document;
    private String scopeName;
    private String collectionName;
    private Map<String, String> documents;

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents upserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[]{
                new UpsertOneStatement(keyspace, document(id, document))
        };
    }
}

