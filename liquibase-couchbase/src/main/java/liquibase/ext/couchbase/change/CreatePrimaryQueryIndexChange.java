package liquibase.ext.couchbase.change;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.change.Change;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.CreatePrimaryQueryIndexStatement;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Part of change set package. Responsible for creating primary query index with specified bucket name, scope name, collection name, index
 * name and other relevant options.
 * @link <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createprimaryindex.html">Reference
 * documentation</a>
 * @see CreatePrimaryQueryIndexStatement
 * @see CreatePrimaryQueryIndexOptions
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@DatabaseChange(
        name = "createPrimaryQueryIndex",
        description = "Create primary query index with validation "
                + "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/createprimaryindex.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"collection", "database"}
)
public class CreatePrimaryQueryIndexChange extends CouchbaseChange {
    private String bucketName;
    private String collectionName;
    private Boolean deferred;
    private String indexName;
    private Integer numReplicas;
    private String scopeName;

    @Override
    public String getConfirmationMessage() {
        return String.format("Primary query index \"%s\" has been created", getIndexName());
    }

    @Override
    public SqlStatement[] generateStatements() {
        if (isNotBlank(getBucketName())) {
            Keyspace keyspace = Keyspace.keyspace(bucketName, scopeName, collectionName);
            return new SqlStatement[] {new CreatePrimaryQueryIndexStatement(keyspace, createPrimaryQueryIndexOptions())};
        }
        return SqlStatement.EMPTY_SQL_STATEMENT;
    }

    private CreatePrimaryQueryIndexOptions createPrimaryQueryIndexOptions() {
        return CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(getIndexName())
                .deferred(getDeferred())
                .numReplicas(getNumReplicas());
    }

    @Override
    protected Change[] createInverses() {
        DropIndexChange inverse = DropIndexChange.builder()
                .bucketName(bucketName)
                .scopeName(scopeName)
                .collectionName(collectionName)
                .indexName(indexName)
                .isPrimary(true)
                .build();

        return new Change[] {inverse};
    }
}