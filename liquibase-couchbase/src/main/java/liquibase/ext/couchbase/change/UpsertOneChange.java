package liquibase.ext.couchbase.change;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.UpsertOneStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 *
 * Part of change set package. Responsible for upserting a single document into a collection.
 *
 * @see UpsertOneStatement
 * @see Keyspace
 *
 * @link <a href="https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html#upsert">Reference documentation</a>
 *
 */

@Data
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
    private String scopeName;
    private String collectionName;
    private Document document;

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents upserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[]{
                new UpsertOneStatement(keyspace, document)
        };
    }
}

