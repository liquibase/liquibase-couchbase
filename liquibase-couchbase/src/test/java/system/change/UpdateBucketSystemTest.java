package system.change;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CompressionMode;
import common.matchers.CouchbaseClusterAssert;
import liquibase.Liquibase;
import liquibase.ext.couchbase.change.UpdateBucketChange;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.time.Duration;

import static common.constants.ChangeLogSampleFilePaths.UPDATE_BUCKET_TEST_XML;

public class UpdateBucketSystemTest extends LiquibaseSystemTest {

    private static String BUCKET_NAME = "updateBucketTest";

    @BeforeAll
    static void setUp() {
        clusterOperator.createBucket(BUCKET_NAME);
    }

    @AfterAll
    static void cleanUp() {
        clusterOperator.dropBucket(BUCKET_NAME);
    }

    @Test
    @SneakyThrows
    void Bucket_should_be_updated() {
        BucketSettings expectedBucketSettings = prepareExpectedSettings();

        Liquibase liquibase = liquibase(UPDATE_BUCKET_TEST_XML);

        liquibase.update();

        CouchbaseClusterAssert.assertThat(cluster).bucketUpdatedSuccessfully(BUCKET_NAME, expectedBucketSettings);
    }

    public static BucketSettings prepareExpectedSettings() {
        return BucketSettings.create(BUCKET_NAME)
                .compressionMode(CompressionMode.PASSIVE)
                .maxExpiry(Duration.ofHours(2))
                .numReplicas(1)
                .ramQuotaMB(100)
                .flushEnabled(false);
    }

}
