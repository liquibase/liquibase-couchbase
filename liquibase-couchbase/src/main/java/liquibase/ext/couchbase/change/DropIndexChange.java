package liquibase.ext.couchbase.change;

import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.DropIndexStatement;
import liquibase.ext.couchbase.statement.DropPrimaryIndexStatement;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * Part of change set package. Responsible for dropping index with specified bucket name, scope name, collection name and index name.
 * @link <a href="https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropindex.html">Reference documentation</a>
 * @see DropIndexStatement
 * @see Keyspace
 */

@DatabaseChange(
        name = "dropQueryIndex",
        description = "Drop query index with validation " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropindex.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"database", "keyspace"}
)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DropIndexChange extends CouchbaseChange {

    private Boolean isPrimary;
    private String indexName;
    private String bucketName;
    private String collectionName;
    private String scopeName;

    @Override
    public String getConfirmationMessage() {
        return String.format("%s dropped for bucket %s",
                isPrimary ? "Primary index" : "Index " + getIndexName(), getBucketName());
    }

    @Override
    public SqlStatement[] generateStatements() {
        Keyspace keyspace = keyspace(bucketName, scopeName, collectionName);
        return new SqlStatement[] {
                isPrimary ? new DropPrimaryIndexStatement(keyspace, indexName) :
                        new DropIndexStatement(indexName, keyspace)
        };
    }

}
