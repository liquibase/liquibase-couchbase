package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreatePrimaryQueryIndexStatementTest {

    private final CreatePrimaryQueryIndexOptions createPrimaryQueryIndexOptions =
            mock(CreatePrimaryQueryIndexOptions.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final Collection collection = mock(Collection.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final CollectionOperator collectionOperator = mock(CollectionOperator.class);


    @Test
    void Should_call_createPrimaryIndex() {
        CreatePrimaryQueryIndexStatement statement =
                new CreatePrimaryQueryIndexStatement(TEST_KEYSPACE, createPrimaryQueryIndexOptions);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollection(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collection);
        when(clusterOperator.getCollectionOperator(collection)).thenReturn(collectionOperator);

        statement.execute(clusterOperator);

        verify(collectionOperator).createPrimaryIndex(createPrimaryQueryIndexOptions);
    }

}