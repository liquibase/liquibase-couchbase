package liquibase.ext.couchbase.change;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.change.utils.BucketUpdateMapper;
import liquibase.ext.couchbase.statement.UpdateBucketStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Part of change set package. Responsible for editing bucket with specified name and options
 * name.
 *
 * @see UpdateBucketStatement
 * @see UpdateBucketOptions
 * @see BucketSettings
 *
 * @link <a href="https://docs.couchbase.com/server/current/manage/manage-buckets/edit-bucket.html">Reference
 * documentation</a>
 *
 */
@Getter
@Setter
@DatabaseChange(
        name = "updateBucket",
        description = "Updates existing bucket, doc: " +
                "https://docs.couchbase.com/server/current/manage/manage-buckets/edit-bucket.html",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"database"}
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBucketChange extends CouchbaseChange {
    private final BucketUpdateMapper mapper = new BucketUpdateMapper(this);

    private Boolean flushEnabled;

    private Integer numReplicas;
    private Long maxExpiryInHours;
    private Long ramQuotaMB;
    private Long timeoutInSeconds;

    private String bucketName;

    private CompressionMode compressionMode;

    @Override
    public String getConfirmationMessage() {
        return String.format("Bucket <%s> has been updated", bucketName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        return new SqlStatement[] {
                new UpdateBucketStatement(mapper.bucketOptions(), mapper.bucketSettings())
        };
    }

}
