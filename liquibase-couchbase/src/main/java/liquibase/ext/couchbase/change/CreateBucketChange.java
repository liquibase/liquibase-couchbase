package liquibase.ext.couchbase.change;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import com.couchbase.client.java.manager.bucket.ConflictResolutionType;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.EvictionPolicyType;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.ext.couchbase.change.utils.BucketCreationMapper;
import liquibase.ext.couchbase.statement.CreateBucketStatement;
import liquibase.statement.SqlStatement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Part of change set package. Responsible for creating bucket with specified name and options
 * name.
 *
 * @see CreateBucketStatement
 * @see CreateBucketOptions
 * @see BucketSettings
 *
 * @link <a href="https://docs.couchbase.com/java-sdk/current/concept-docs/buckets-and-clusters.html#creating-and-removing-buckets">Reference
 * documentation</a>
 *
 */
@Getter
@Setter
@DatabaseChange(
        name = "createBucket",
        description = "Create bucket with validation, doc: " +
                "https://docs.couchbase.com/java-sdk/current/concept-docs/buckets-and-clusters.html#creating-and-removing-buckets",
        priority = ChangeMetaData.PRIORITY_DEFAULT,
        appliesTo = {"database"}
)
@NoArgsConstructor
@AllArgsConstructor
public class CreateBucketChange extends CouchbaseChange {
    private final BucketCreationMapper mapper = new BucketCreationMapper(this);

    private Boolean flushEnabled;
    private Boolean replicaIndexes;

    private Integer numReplicas;
    private Long maxExpiryInHours;
    private Long ramQuotaMB;
    private Long timeoutInSeconds;

    private String bucketName;
    private String storageBackend;

    private BucketType bucketType;
    private CompressionMode compressionMode;
    private ConflictResolutionType conflictResolutionType;
    private EvictionPolicyType evictionPolicy;
    private DurabilityLevel minimumDurabilityLevel;

    @Override
    public String getConfirmationMessage() {
        return String.format("Bucket <%s> has been created", bucketName);
    }

    @Override
    public SqlStatement[] generateStatements() {
        return new SqlStatement[]{
                new CreateBucketStatement(mapper.bucketOptions(), mapper.bucketSettings())
        };
    }

}
