package liquibase.ext.couchbase.change.utils;

import java.time.Duration;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.StorageBackend;
import liquibase.ext.couchbase.change.CreateBucketChange;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.couchbase.client.java.manager.bucket.CreateBucketOptions.createBucketOptions;

@Builder
@RequiredArgsConstructor
public class BucketCreationMapper {
    @Getter
    private final CreateBucketChange change;

    public CreateBucketOptions bucketOptions() {
        return createBucketOptions().timeout(Duration.ofSeconds(change.getTimeoutInSeconds()));
    }

    public BucketSettings bucketSettings() {
        BucketSettings bucketSettings = BucketSettings.create(change.getBucketName())
                .flushEnabled(change.getFlushEnabled())
                .ramQuotaMB(change.getRamQuotaMB())
                .numReplicas(change.getNumReplicas())
                .replicaIndexes(change.getReplicaIndexes())
                .bucketType(change.getBucketType())
                .evictionPolicy(change.getEvictionPolicy())
                .minimumDurabilityLevel(change.getMinimumDurabilityLevel())
                .storageBackend(StorageBackend.of(change.getStorageBackend()));
        if (change.getCompressionMode() != null) {
            bucketSettings = bucketSettings.compressionMode(change.getCompressionMode());
        }
        if (change.getConflictResolutionType() != null) {
            bucketSettings = bucketSettings.conflictResolutionType(change.getConflictResolutionType());
        }
        if (change.getMaxExpiryInHours() != null) {
            bucketSettings = bucketSettings.maxExpiry(Duration.ofHours(change.getMaxExpiryInHours()));
        }
        return bucketSettings;
    }

}
