package liquibase.ext.couchbase.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class MutateInQueryStatementTest {

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
        String whereClause = "where";
        List<String> documentIds = Arrays.asList("docId1", "docId2");
        MutateInQueryStatement statement = new MutateInQueryStatement(mutate, mutateInOptions, whereClause);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollection(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collection);
        when(clusterOperator.retrieveDocumentIdsByWhereClause(TEST_KEYSPACE, whereClause)).thenReturn(documentIds);
        statement.execute(clusterOperator);

        verify(collection).mutateIn(documentIds.get(0), mutate.getSpecs(), mutateInOptions);
        verify(collection).mutateIn(documentIds.get(1), mutate.getSpecs(), mutateInOptions);
    }

}