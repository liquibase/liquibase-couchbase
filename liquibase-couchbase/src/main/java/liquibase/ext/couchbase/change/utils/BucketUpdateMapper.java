package liquibase.ext.couchbase.change.utils;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import liquibase.ext.couchbase.change.UpdateBucketChange;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

import static com.couchbase.client.java.manager.bucket.UpdateBucketOptions.updateBucketOptions;

@Builder
@RequiredArgsConstructor
public class BucketUpdateMapper {
    @Getter
    private final UpdateBucketChange change;

    public UpdateBucketOptions bucketOptions() {
        return updateBucketOptions().timeout(Duration.ofSeconds(change.getTimeoutInSeconds()));
    }

    public BucketSettings bucketSettings() {
        return BucketSettings.create(change.getBucketName())
                .compressionMode(change.getCompressionMode())
                .flushEnabled(change.getFlushEnabled())
                .ramQuotaMB(change.getRamQuotaMB())
                .numReplicas(change.getNumReplicas())
                .maxExpiry(Duration.ofHours(change.getMaxExpiryInHours()));
    }

}
