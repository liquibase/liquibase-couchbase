package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
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
    private final Bucket bucket = mock(Bucket.class);
    private final Scope scope = mock(Scope.class);
    private final Collection collection = mock(Collection.class);

    @Test
    void Should_call_dropIndex_if_indexName_exists() {
        String indexName = "indexName";

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_KEYSPACE, indexName);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getBucket()).thenReturn(bucket);
        when(bucket.scope(TEST_KEYSPACE.getScope())).thenReturn(scope);
        when(scope.collection(TEST_KEYSPACE.getCollection())).thenReturn(collection);

        when(clusterOperator.getCollectionOperator(collection)).thenReturn(collectionOperator);

        statement.execute(clusterOperator);

        verify(collectionOperator).dropIndex(indexName);
    }

    @Test
    void Should_call_dropCollectionPrimaryIndex_if_indexName_not_exists() {
        String indexName = null;

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_KEYSPACE, indexName);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getBucket()).thenReturn(bucket);
        when(bucket.scope(TEST_KEYSPACE.getScope())).thenReturn(scope);
        when(scope.collection(TEST_KEYSPACE.getCollection())).thenReturn(collection);

        when(clusterOperator.getCollectionOperator(collection)).thenReturn(collectionOperator);

        statement.execute(clusterOperator);

        verify(collectionOperator).dropCollectionPrimaryIndex();
    }

}