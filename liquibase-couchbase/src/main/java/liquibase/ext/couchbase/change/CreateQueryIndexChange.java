package liquibase.ext.couchbase.change;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.couchbase.statement.CreateQueryIndexStatement;
import liquibase.ext.couchbase.types.Field;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * Part of change set package. Responsible for creating query index with specified bucket name, scope name,
 * collection name, index name and other relevant options.<br><br>
 *
 * @apiNote Compound index can be created by specifying multiple fields in the list of fields.
 *
 * @see CreateQueryIndexStatement
 * @see CreateQueryIndexOptions
 *
 * @link <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createindex.html">Reference
 * documentation</a>
 *
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DatabaseChange(
        name = "createQueryIndex",
        description = "Create query index with validation " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createindex.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"collection", "database"}
)
public class CreateQueryIndexChange extends CouchbaseChange {

    private String bucketName;
    private String indexName;
    private List<Field> fields = new ArrayList<>();
    private String collectionName;
    private String scopeName;
    private Boolean deferred;
    private Integer numReplicas;
    private Boolean ignoreIfExists;

    @Override
    public String getConfirmationMessage() {
        return String.format("Query index \"%s\" has been created", getIndexName());
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                new CreateQueryIndexStatement(getBucketName(), getIndexName(), fields, createQueryIndexOptions())
        };
    }

    private CreateQueryIndexOptions createQueryIndexOptions() {
        return CreateQueryIndexOptions
                .createQueryIndexOptions()
                .collectionName(getCollectionName())
                .scopeName(getScopeName())
                .deferred(getDeferred())
                .numReplicas(getNumReplicas())
                .ignoreIfExists(getIgnoreIfExists());
    }
}