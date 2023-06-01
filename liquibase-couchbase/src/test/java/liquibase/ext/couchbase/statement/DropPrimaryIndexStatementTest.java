package liquibase.ext.couchbase.statement;

import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DropPrimaryIndexStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final CollectionOperator collectionOperator = mock(CollectionOperator.class);

    @Test
    void Should_call_dropIndex_if_indexName_exists() {
        String indexName = "indexName";

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_KEYSPACE, indexName);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collectionOperator);

        statement.execute(clusterOperator);

        verify(collectionOperator).dropIndex(indexName);
    }

    @Test
    void Should_call_dropCollectionPrimaryIndex_if_indexName_not_exists() {
        String indexName = null;

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_KEYSPACE, indexName);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collectionOperator);

        statement.execute(clusterOperator);

        verify(collectionOperator).dropCollectionPrimaryIndex();
    }

}