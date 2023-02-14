package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.Keyspace;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.CreateCollectionStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * Part of change set package. Responsible for creating collection with specified bucket name, scope name and collection
 * name.
 *
 * @see CreateCollectionStatement
 * @see Keyspace
 *
 * @link <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createcollection.html">Reference
 * documentation</a>
 *
 */

@Getter
@Setter
@DatabaseChange(
        name = "createCollection",
        description = "Create collection with validation, doc: " +
        "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createcollection.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"database", "bucket"}
)
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionChange extends CouchbaseChange {

    private String bucketName;
    private String scopeName;
    private String collectionName;
    private Boolean skipIfExists;

    @Override
    public String getConfirmationMessage() {
        return String.format("%s has been successfully created", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[]{
                new CreateCollectionStatement(keyspace, skipIfExists)
        };
    }
}
