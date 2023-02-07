package liquibase.ext.change;

import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.statement.DropIndexStatement;
import liquibase.ext.statement.DropPrimaryIndexStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@DatabaseChange(
        name = "dropQueryIndex",
        description = "Drop query index with validation " +
                "https://docs.couchbase.com/server/current/n1ql/n1ql-language-reference/dropindex.html",
        priority = PrioritizedService.PRIORITY_DATABASE,
        appliesTo = {"database", "keyspace"}
)
@Getter
@EqualsAndHashCode(callSuper = true)
@RequiredArgsConstructor
public class DropIndexChange extends CouchbaseChange {
    private final boolean isPrimary;
    private final String indexName;
    private final String bucketName;
    private final String collectionName;
    private final String scopeName;

    @Override
    public String getConfirmationMessage() {
        return String.format("%s dropped for bucket %s",
                isPrimary() ? "Primary index" : "Index " + getIndexName(), getBucketName());
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {
        return new SqlStatement[]{
                isPrimary ? new DropPrimaryIndexStatement(bucketName, collectionName, scopeName) :
                        new DropIndexStatement(indexName, bucketName, collectionName, scopeName)
        };
    }
}

