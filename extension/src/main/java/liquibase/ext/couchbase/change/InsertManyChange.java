package liquibase.ext.couchbase.change;


import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.InsertManyStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@DatabaseChange(
        name = "insertMany",
        description = "Inserts multiple documents into a collection https://docs.couchbase.com/java-sdk/3.3/howtos/kv-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class InsertManyChange extends CouchbaseChange {

    private String bucketName;
    private String id;
    private String scopeName;
    private String collectionName;
    private Map<String, String> documents;

    @Override
    public String getConfirmationMessage() {
        return String.format("Documents inserted into collection %s", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                new InsertManyStatement(bucketName, documents, scopeName, collectionName)
        };
    }
}

