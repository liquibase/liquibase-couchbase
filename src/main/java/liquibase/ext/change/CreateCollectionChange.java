package liquibase.ext.change;

import liquibase.ext.statement.CreateCollectionStatement;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@DatabaseChange(
        name = "createCollection",
        description = "Create collection with validation ",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = "collection"
)
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectionChange extends CouchbaseChange {

    private String collectionName;

    @Override
    public String getConfirmationMessage() {
        return String.format("%s has successfully created", collectionName);
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                new CreateCollectionStatement(collectionName)
        };
    }
}
