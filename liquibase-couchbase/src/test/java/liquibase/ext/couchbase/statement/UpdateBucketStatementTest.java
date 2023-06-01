package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import liquibase.ext.couchbase.operator.ClusterOperator;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UpdateBucketStatementTest {

    private final BucketSettings bucketSettings = mock(BucketSettings.class);
    private final UpdateBucketOptions updateBucketOptions = mock(UpdateBucketOptions.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);

    @Test
    void Should_call_updateBucketWithOptionsAndSettings() {
        UpdateBucketStatement statement = new UpdateBucketStatement(updateBucketOptions, bucketSettings);

        statement.execute(clusterOperator);
        verify(clusterOperator).updateBucketWithOptionsAndSettings(bucketSettings, updateBucketOptions);
    }

}