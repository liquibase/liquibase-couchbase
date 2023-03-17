package common.change;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import com.couchbase.client.java.manager.bucket.ConflictResolutionType;
import com.couchbase.client.java.manager.bucket.EvictionPolicyType;
import liquibase.ext.couchbase.change.CreateBucketChange;
import liquibase.ext.couchbase.change.UpdateBucketChange;

public class TestChangeFactory {

    public static CreateBucketChange prepareCreateBucketChange(String bucketName) {
        return CreateBucketChange.builder()
                .bucketName(bucketName).bucketType(BucketType.COUCHBASE)
                .replicaIndexes(false)
                .conflictResolutionType(ConflictResolutionType.SEQUENCE_NUMBER)
                .compressionMode(CompressionMode.OFF).maxExpiryInHours(1L)
                .storageBackend("couchstore")
                .evictionPolicy(EvictionPolicyType.VALUE_ONLY)
                .minimumDurabilityLevel(DurabilityLevel.NONE)
                .numReplicas(0).ramQuotaMB(128L).flushEnabled(true)
                .timeoutInSeconds(10L).build();
    }

    public static UpdateBucketChange prepareUpdateBucketChange(String bucketName) {
        return UpdateBucketChange.builder()
                .bucketName(bucketName).compressionMode(CompressionMode.ACTIVE)
                .maxExpiryInHours(2L).numReplicas(1).ramQuotaMB(256L)
                .flushEnabled(false).timeoutInSeconds(20L).build();
    }
}
