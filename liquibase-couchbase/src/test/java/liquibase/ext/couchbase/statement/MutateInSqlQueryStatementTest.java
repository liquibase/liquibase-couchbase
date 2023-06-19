package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.MutateInOptions;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.subdoc.MutateIn;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MutateInSqlQueryStatementTest {

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
        String query = "query";
        Set<String> documentIds = new HashSet<>(Arrays.asList("docId1", "docId2"));
        MutateInSqlQueryStatement statement = new MutateInSqlQueryStatement(mutate, mutateInOptions, query);

        when(clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())).thenReturn(bucketOperator);
        when(bucketOperator.getCollection(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())).thenReturn(
                collection);
        when(clusterOperator.retrieveDocumentIdsBySqlPlusPlusQuery(query)).thenReturn(documentIds);
        statement.execute(clusterOperator);
        documentIds.stream().forEach(id -> verify(collection).mutateIn(id, mutate.getSpecs(), mutateInOptions));
    }
}