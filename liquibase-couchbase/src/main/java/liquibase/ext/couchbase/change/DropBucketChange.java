package liquibase.ext.couchbase.change;

import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.statement.DropBucketStatement;
import liquibase.servicelocator.PrioritizedService;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Part of change set package. Responsible for dropping bucket with specified name
 *
 * @see DropBucketStatement
 */
@DatabaseChange(name = "dropBucket",
                description = "Drop bucket with validation " + "https://docs.couchbase.com/server/current/manage/manage-buckets/delete" +
                        "-bucket.html#dropping-a-bucket",
                priority = PrioritizedService.PRIORITY_DATABASE,
                appliesTo = {"database"})
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DropBucketChange extends CouchbaseChange {

    private String bucketName;

    @Override
    public String getConfirmationMessage() {
        return String.format("The '%s' bucket has been dropped successfully", getBucketName());
    }

    @Override
    public SqlStatement[] generateStatements() {
        return new SqlStatement[] {new DropBucketStatement(bucketName)};
    }

}