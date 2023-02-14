package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.Keyspace;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.InsertOneStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static liquibase.ext.couchbase.types.Document.document;

/**
 *
 * Part of change set package. Responsible for inserting a single document into a collection.
 *
 * @see InsertOneStatement
 * @see Keyspace
 *
 * @link <a href="https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html#insert">Reference documentation</a>
 *
 */

@Getter
@Setter
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
        return new SqlStatement[]{new InsertOneStatement(keyspace, document(id, document))
        };
    }
}
