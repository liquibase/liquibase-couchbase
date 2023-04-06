package liquibase.ext.couchbase.change.utils;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.BucketType;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import com.couchbase.client.java.manager.bucket.ConflictResolutionType;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.EvictionPolicyType;
import com.couchbase.client.java.manager.bucket.StorageBackend;
import liquibase.ext.couchbase.change.CreateBucketChange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Duration;

import static com.couchbase.client.java.manager.bucket.CreateBucketOptions.createBucketOptions;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@MockitoSettings(strictness = LENIENT)
class BucketCreationMapperTest {
    private final long TEST_TIMEOUT = 30;
    private final long TEST_RAM_QUOTA = 100;
    private final int TEST_NUM_REPLICAS = 2;
    private final long TEST_EXPIRY_IN_HOURS = 24;

    private final CreateBucketOptions expected = createBucketOptions().timeout(Duration.ofSeconds(TEST_TIMEOUT));

    @Mock
    private CreateBucketChange createBucketChange;

    @InjectMocks
    private BucketCreationMapper creationMapper;

    @Test
    void testBucketOptions() {
        when(createBucketChange.getTimeoutInSeconds()).thenReturn(TEST_TIMEOUT);

        CreateBucketOptions result = creationMapper.bucketOptions();

        assertEquals(expected.build().timeout(), result.build().timeout());
    }

    @Test
    void testBucketSettings() {
        BucketSettings expected = createExpected();

        when(createBucketChange.getBucketName()).thenReturn(TEST_BUCKET);
        when(createBucketChange.getCompressionMode()).thenReturn(CompressionMode.PASSIVE);
        when(createBucketChange.getFlushEnabled()).thenReturn(true);
        when(createBucketChange.getRamQuotaMB()).thenReturn(TEST_RAM_QUOTA);
        when(createBucketChange.getNumReplicas()).thenReturn(TEST_NUM_REPLICAS);
        when(createBucketChange.getReplicaIndexes()).thenReturn(true);
        when(createBucketChange.getMaxExpiryInHours()).thenReturn(TEST_EXPIRY_IN_HOURS);
        when(createBucketChange.getBucketType()).thenReturn(BucketType.COUCHBASE);
        when(createBucketChange.getConflictResolutionType()).thenReturn(ConflictResolutionType.CUSTOM);
        when(createBucketChange.getEvictionPolicy()).thenReturn(EvictionPolicyType.FULL);
        when(createBucketChange.getMinimumDurabilityLevel()).thenReturn(DurabilityLevel.MAJORITY);
        when(createBucketChange.getStorageBackend()).thenReturn(StorageBackend.COUCHSTORE.toString());

        BucketSettings result = creationMapper.bucketSettings();

        assertEquals(expected.name(), result.name());
        assertEquals(expected.flushEnabled(), result.flushEnabled());
        assertEquals(expected.ramQuotaMB(), result.ramQuotaMB());
        assertEquals(expected.numReplicas(), result.numReplicas());
        assertEquals(expected.replicaIndexes(), result.replicaIndexes());
        assertEquals(expected.maxExpiry(), result.maxExpiry());
        assertEquals(expected.bucketType(), result.bucketType());
        assertEquals(expected.conflictResolutionType(), result.conflictResolutionType());
        assertEquals(expected.evictionPolicy(), result.evictionPolicy());
        assertEquals(expected.minimumDurabilityLevel(), result.minimumDurabilityLevel());
        assertEquals(expected.storageBackend(), result.storageBackend());
    }

    private BucketSettings createExpected() {
        return BucketSettings.create(TEST_BUCKET)
                .flushEnabled(true)
                .compressionMode(CompressionMode.PASSIVE)
                .ramQuotaMB(TEST_RAM_QUOTA)
                .numReplicas(TEST_NUM_REPLICAS)
                .replicaIndexes(true)
                .maxExpiry(Duration.ofHours(TEST_EXPIRY_IN_HOURS))

                .bucketType(BucketType.COUCHBASE)
                .conflictResolutionType(ConflictResolutionType.CUSTOM)
                .evictionPolicy(EvictionPolicyType.FULL)
                .minimumDurabilityLevel(DurabilityLevel.MAJORITY)
                .storageBackend(StorageBackend.COUCHSTORE);
    }
}