package liquibase.ext.couchbase.change;

import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.ExecuteQueryStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Responsible for executing sql++ query
 * @link <a href="https://docs.couchbase.com/java-sdk/current/howtos/n1ql-queries-with-sdk.html">Reference documentation</a>
 * @see ExecuteQueryStatement
 */

@DatabaseChange(
        name = "executeQuery",
        description = "Execute sql++ query " +
                "https://docs.couchbase.com/java-sdk/current/howtos/n1ql-queries-with-sdk.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"database"}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExecuteQueryChange extends CouchbaseChange {
    private String query;

    @Override
    public String getConfirmationMessage() {
        return String.format("Query %s has been successfully executed", query);
    }

    @Override
    public SqlStatement[] generateStatements() {
        return new SqlStatement[] {
                new ExecuteQueryStatement(query)
        };
    }

}
