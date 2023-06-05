package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import liquibase.ext.couchbase.operator.ClusterOperator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateBucketStatementTest {

    private final BucketSettings bucketSettings = mock(BucketSettings.class);
    private final CreateBucketOptions createBucketOptions = mock(CreateBucketOptions.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);

    @Test
    void Should_call_createBucket() {
        CreateBucketStatement statement = new CreateBucketStatement(createBucketOptions, bucketSettings);

        statement.execute(clusterOperator);
        verify(clusterOperator).createBucketWithOptionsAndSettings(bucketSettings, createBucketOptions);
    }

}