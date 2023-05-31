package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.ClusterOperator;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class DropBucketStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);

    @Test
    void Should_call_dropBucket() {
        DropBucketStatement statement = new DropBucketStatement(TEST_BUCKET);

        statement.execute(clusterOperator);
        verify(clusterOperator).dropBucket(TEST_BUCKET);
    }

}