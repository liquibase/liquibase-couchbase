package liquibase.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.couchbase.client.core.diagnostics.PingResult;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.manager.bucket.BucketSettings;

import org.junit.jupiter.api.Test;

/**
 * Todo
 */
public class ConnectionTest extends CouchbaseContainerizedTest {

    @Test
    void connect_couchbase_sucessfully() {
        cluster.buckets().createBucket(BucketSettings.create("travels"));
        Bucket travels = cluster.bucket("travels");

        PingResult ping = travels.ping();

        assertThat(ping.id()).isNotEmpty();
    }
}
