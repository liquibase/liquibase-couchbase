package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DropScopeStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);

    @Test
    void Should_call_dropScope() {
        DropScopeStatement statement = new DropScopeStatement(TEST_SCOPE, TEST_BUCKET);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);

        statement.execute(clusterOperator);

        verify(bucketOperator).dropScope(TEST_SCOPE);
    }

}