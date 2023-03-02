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
                .compressionMode(CompressionMode.OFF).maxExpiryInHours((long) 1)
                .storageBackend("couchstore")
                .evictionPolicy(EvictionPolicyType.VALUE_ONLY)
                .minimumDurabilityLevel(DurabilityLevel.NONE)
                .numReplicas(0).ramQuotaMB((long) 128).flushEnabled(true)
                .timeoutInSeconds((long) 10).build();
    }

    public static UpdateBucketChange prepareUpdateBucketChange(String bucketName) {
        return UpdateBucketChange.builder()
                .bucketName(bucketName).compressionMode(CompressionMode.ACTIVE)
                .maxExpiryInHours((long) 2).numReplicas(1).ramQuotaMB((long) 256)
                .flushEnabled(false).timeoutInSeconds((long) 20).build();
    }
}
