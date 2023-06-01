package liquibase.ext.couchbase.statement;

import java.util.ArrayList;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutateInOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MutateInStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final MutateInOptions mutateInOptions = mock(MutateInOptions.class);
    private final Collection collection = mock(Collection.class);

    @Test
    void Should_call_mutateIn() {
        MutateIn mutate = MutateIn.builder()
                .keyspace(TEST_KEYSPACE)
                .id("id")
                .specs(new ArrayList<>())
                .build();
        MutateInStatement statement = new MutateInStatement(mutate, mutateInOptions);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollection(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collection);

        statement.execute(clusterOperator);

        verify(collection).mutateIn(mutate.getId(), mutate.getSpecs(), mutateInOptions);
    }

}