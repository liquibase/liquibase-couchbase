package liquibase.ext.couchbase.precondition;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.BucketNotExistsPreconditionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BucketExistsPreconditionTest {

    private final Database database = mock(Database.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final BucketManager bucketManager = mock(BucketManager.class);

    @BeforeEach
    public void configure() {
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(database.getConnection()).thenReturn(connection);
    }

    @Test
    @SneakyThrows
    void Should_pass_when_bucket_exists() {
        BucketExistsPrecondition precondition = new BucketExistsPrecondition();
        precondition.setBucketName(NEW_TEST_BUCKET);
        BucketSettings settings = BucketSettings.create(NEW_TEST_BUCKET);
        when(bucketManager.getBucket(NEW_TEST_BUCKET)).thenReturn(settings);
        precondition.check(database, null, null, null);
        verify(bucketManager).getBucket(precondition.getBucketName());
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_bucket_not_exists() {
        BucketExistsPrecondition precondition = new BucketExistsPrecondition();
        precondition.setBucketName(NEW_TEST_BUCKET);
        when(bucketManager.getBucket(NEW_TEST_BUCKET)).thenThrow(new BucketNotFoundException("aaa"));
        assertThatExceptionOfType(BucketNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Bucket newTestBucket does not exist");
    }
}