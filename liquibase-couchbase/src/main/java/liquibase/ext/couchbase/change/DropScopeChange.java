package liquibase.ext.couchbase.change;

import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.DropScopeStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Part of change set package. Responsible for drop scope with specified name
 * @link <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropscope.html">Reference documentation</a>
 * @see DropScopeStatement
 */

@DatabaseChange(
        name = "dropScope",
        description = "Drop scope with validation " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropscope.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"database"}
)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DropScopeChange extends CouchbaseChange {
    private String bucketName;
    private String scopeName;

    @Override
    public String getConfirmationMessage() {
        return String.format("Scope %s has been successfully dropped", scopeName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        return new SqlStatement[] {
                new DropScopeStatement(scopeName, bucketName)
        };
    }

}
