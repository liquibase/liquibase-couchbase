package liquibase.ext.couchbase.change;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.DropCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;


@Getter
@Setter
@DatabaseChange(
        name = "dropCollection",
        description = "Drop collection with validation, doc: " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropcollection.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"bucket"}
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DropCollectionChange extends CouchbaseChange {

    private String bucketName;
    private String scopeName;
    private String collectionName;

    @Override
    public String getConfirmationMessage() {
        return String.format("Collection %s has been successfully dropped", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[] {new DropCollectionStatement(keyspace)};
    }
}
