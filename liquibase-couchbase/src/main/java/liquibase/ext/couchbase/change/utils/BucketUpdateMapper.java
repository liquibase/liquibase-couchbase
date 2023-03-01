package liquibase.ext.couchbase.change.utils;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import liquibase.ext.couchbase.change.UpdateBucketChange;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Data
@RequiredArgsConstructor
public class BucketUpdateMapper {
    private final UpdateBucketChange change;

    public UpdateBucketOptions bucketOptions() {
        return UpdateBucketOptions.updateBucketOptions()
                .timeout(Duration.ofSeconds(change.getTimeoutInSeconds()));
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
