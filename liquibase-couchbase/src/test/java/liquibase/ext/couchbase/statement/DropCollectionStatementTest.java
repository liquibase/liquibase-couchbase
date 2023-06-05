package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DropCollectionStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);

    @Test
    void Should_call_dropCollection() {
        DropCollectionStatement statement = new DropCollectionStatement(TEST_KEYSPACE);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);

        statement.execute(clusterOperator);

        verify(bucketOperator).dropCollection(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope());
    }

}