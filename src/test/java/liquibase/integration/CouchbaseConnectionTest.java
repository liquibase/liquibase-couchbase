package liquibase.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.couchbase.client.core.diagnostics.PingResult;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CouchbaseConnectionTest extends CouchbaseContainerizedTest {

    private static final String TEST_BUCKET = "travels";
    protected Cluster cluster;

    @BeforeEach
    void setUpLocal() {
        cluster = Cluster.connect(
                container.getConnectionString(),
                container.getUsername(),
                container.getPassword()
        );
    }

    @AfterEach
    void tearDownLocal() {
        cluster.close();
    }

    @Test
    void connect_couchbase_sucessfully() {
        cluster.buckets().createBucket(BucketSettings.create(TEST_BUCKET));
        Bucket travels = cluster.bucket(TEST_BUCKET);

        PingResult ping = travels.ping();

        assertThat(ping.id()).isNotEmpty();
        cluster.buckets().dropBucket(TEST_BUCKET);
    }
}
