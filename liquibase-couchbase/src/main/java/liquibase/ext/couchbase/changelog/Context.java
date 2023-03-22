package liquibase.ext.couchbase.changelog;

import liquibase.ContextExpression;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * Class for storing history context information. For more details what is context look in the link below.
 * @link <a href="https://docs.liquibase.com/concepts/changelogs/attributes/contexts.html">Liquibase contexts</a>
 */
@Data
@NoArgsConstructor
public class Context {

    private Set<String> contexts;
    private String originalString;

    public Context(ContextExpression contextExpression) {
        this.contexts = contextExpression.getContexts();
        this.originalString = contextExpression.getOriginalString();
    }

}
