package liquibase.ext.couchbase.change;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@DatabaseChange(
        name = "mutateIn",
        description = "https://docs.couchbase.com/java-sdk/current/howtos/subdocument-operations.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"collection", "database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class MutateInChange extends CouchbaseChange {

    private String id;

    private String bucketName;
    private String scopeName;
    private String collectionName;

    private List<LiquibaseMutateInSpec> mutateInSpecs = new ArrayList<>();

    @Override
    public SqlStatement[] generateStatements(Database database) {
        //todo
        throw new RuntimeException();
    }

    @Override
    public String getConfirmationMessage() {
        //todo
        throw new RuntimeException();
    }
}
