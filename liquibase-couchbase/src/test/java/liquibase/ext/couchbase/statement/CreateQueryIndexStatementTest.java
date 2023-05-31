package liquibase.ext.couchbase.statement;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Field;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateQueryIndexStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final Collection collection = mock(Collection.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final CollectionOperator collectionOperator = mock(CollectionOperator.class);

    @Test
    void Should_call_createQueryIndex() {
        String indexName = "indexName";
        boolean deferred = false;
        int numReplicas = 99;
        List<Field> fields = Arrays.asList(new Field("field1"), new Field("field2"));

        CreateQueryIndexStatement statement =
                new CreateQueryIndexStatement(indexName, TEST_KEYSPACE, deferred, numReplicas, fields);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollection(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collection);
        when(clusterOperator.getCollectionOperator(collection)).thenReturn(collectionOperator);

        statement.execute(clusterOperator);

        verify(collectionOperator).createQueryIndex(eq(indexName), eq(fields), any());
    }

}