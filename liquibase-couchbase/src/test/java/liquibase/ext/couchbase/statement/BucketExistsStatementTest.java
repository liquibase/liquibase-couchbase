package liquibase.ext.couchbase.statement;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BucketExistsStatementTest {

    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final BucketManager bucketManager = mock(BucketManager.class);

    @BeforeEach
    public void configure() {
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
    }

    @Test
    void Should_return_true_if_bucket_exists() {
        BucketExistsStatement statement = new BucketExistsStatement(TEST_BUCKET);

        BucketSettings bucketSettings = mock(BucketSettings.class);
        when(bucketManager.getBucket(TEST_BUCKET)).thenReturn(bucketSettings);

        assertThat(statement.isTrue(connection)).isTrue();
    }

    @Test
    void Should_return_false_if_bucket_not_found() {
        BucketExistsStatement statement = new BucketExistsStatement(TEST_BUCKET);

        when(bucketManager.getBucket(TEST_BUCKET)).thenThrow(new BucketNotFoundException(TEST_BUCKET));

        assertThat(statement.isTrue(connection)).isFalse();
    }
}