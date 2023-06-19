package liquibase.ext.couchbase.change.utils;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import liquibase.ext.couchbase.change.UpdateBucketChange;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Duration;

import static com.couchbase.client.java.manager.bucket.UpdateBucketOptions.updateBucketOptions;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.quality.Strictness.LENIENT;

@MockitoSettings(strictness = LENIENT)
class BucketUpdateMapperTest {
    private final long TEST_TIMEOUT = 30;
    private final long TEST_RAM_QUOTA = 100;
    private final int TEST_NUM_REPLICAS = 2;
    private final long TEST_EXPIRY_IN_HOURS = 24;

    private final UpdateBucketOptions.Built expected = updateBucketOptions().timeout(Duration.ofSeconds(TEST_TIMEOUT)).build();

    @Mock
    private UpdateBucketChange updateBucketChange;

    @InjectMocks
    private BucketUpdateMapper bucketUpdateMapper;

    @Test
    void testBucketOptions() {
        when(updateBucketChange.getTimeoutInSeconds()).thenReturn(TEST_TIMEOUT);

        UpdateBucketOptions result = bucketUpdateMapper.bucketOptions();

        assertEquals(expected.timeout(), result.build().timeout());
    }

    @Test
    void testBucketSettings() {
        BucketSettings expected = createExpected();

        when(updateBucketChange.getBucketName()).thenReturn(TEST_BUCKET);
        when(updateBucketChange.getCompressionMode()).thenReturn(CompressionMode.PASSIVE);
        when(updateBucketChange.getFlushEnabled()).thenReturn(true);
        when(updateBucketChange.getRamQuotaMB()).thenReturn(TEST_RAM_QUOTA);
        when(updateBucketChange.getNumReplicas()).thenReturn(TEST_NUM_REPLICAS);
        when(updateBucketChange.getMaxExpiryInHours()).thenReturn(TEST_EXPIRY_IN_HOURS);

        BucketSettings result = bucketUpdateMapper.bucketSettings();
        assertEquals(expected.name(), result.name());
        assertEquals(expected.flushEnabled(), result.flushEnabled());
        assertEquals(expected.ramQuotaMB(), result.ramQuotaMB());
        assertEquals(expected.numReplicas(), result.numReplicas());
        assertEquals(expected.maxExpiry(), result.maxExpiry());
    }

    private BucketSettings createExpected() {
        return BucketSettings.create(TEST_BUCKET)
                .flushEnabled(true)
                .compressionMode(CompressionMode.PASSIVE)
                .ramQuotaMB(TEST_RAM_QUOTA)
                .numReplicas(TEST_NUM_REPLICAS)
                .maxExpiry(Duration.ofHours(TEST_EXPIRY_IN_HOURS));
    }
}