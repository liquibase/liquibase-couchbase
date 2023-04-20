package liquibase.ext.couchbase.change;

import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.CreateScopeStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * Part of change set package. Responsible for create scope with specified name
 * @link <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createscope.html">Reference documentation</a>
 * @see CreateScopeStatement
 */

@DatabaseChange(
        name = "createScope",
        description = "Create scope with validation " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createscope.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"database"}
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateScopeChange extends CouchbaseChange {
    private String bucketName;
    private String scopeName;

    @Override
    public String getConfirmationMessage() {
        return String.format("Scope %s has been successfully created", scopeName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        return new SqlStatement[] {
                new CreateScopeStatement(scopeName, bucketName)
        };
    }

}
