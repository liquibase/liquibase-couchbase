package liquibase.ext.couchbase.change;


import com.wdt.couchbase.Keyspace;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.wdt.couchbase.Keyspace.keyspace;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DatabaseChange(
        name = "insertOne",
        description = "Insert a single document into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class InsertOneChange extends CouchbaseChange {

    private String bucketName;
    private String id;
    private String document;
    private String scopeName;
    private String collectionName;

    @Override
    public String getConfirmationMessage() {
        return String.format("Document inserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[]{new InsertOneStatement(keyspace, id, document)
        };
    }
}
