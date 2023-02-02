package liquibase.ext.change;

import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.statement.DropIndexStatement;
import liquibase.ext.statement.DropPrimaryIndexStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@DatabaseChange(
        name = "dropQueryIndex",
        description = "Drop query index with validation " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropindex.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"collection", "database"}
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DropIndexChange extends CouchbaseChange {
    private boolean isPrimary = true;
    private String indexName;
    private String bucketName;
    private String collectionName;
    private String scopeName;

    @Override
    public String getConfirmationMessage() {
        return (isPrimary() ? "Primary index" : "Index " + getIndexName()) +
                " dropped for bucket " + getBucketName();
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                isPrimary ? new DropPrimaryIndexStatement(bucketName, collectionName, scopeName) :
                        new DropIndexStatement(indexName, bucketName, collectionName, scopeName)
        };
    }
}

