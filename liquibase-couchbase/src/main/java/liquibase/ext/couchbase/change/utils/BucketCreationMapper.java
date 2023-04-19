package liquibase.ext.couchbase.change.utils;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.StorageBackend;
import liquibase.ext.couchbase.change.CreateBucketChange;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

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
        return BucketSettings.create(change.getBucketName())
                .compressionMode(change.getCompressionMode())
                .flushEnabled(change.getFlushEnabled())
                .ramQuotaMB(change.getRamQuotaMB())
                .numReplicas(change.getNumReplicas())
                .replicaIndexes(change.getReplicaIndexes())
                .maxExpiry(Duration.ofHours(change.getMaxExpiryInHours()))
                .bucketType(change.getBucketType())
                .conflictResolutionType(change.getConflictResolutionType())
                .evictionPolicy(change.getEvictionPolicy())
                .minimumDurabilityLevel(change.getMinimumDurabilityLevel())
                .storageBackend(StorageBackend.of(change.getStorageBackend()));
    }

}
