package liquibase.ext.couchbase.statement;

import java.util.Arrays;
import java.util.List;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Param;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExecuteQueryStatementTest {

    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final Cluster cluster = mock(Cluster.class);

    @Test
    void Should_call_query_if_params_not_empty() {
        String query = "query";
        List<Param> params = Arrays.asList(Param.builder().name("param1").value(1L).build(),
                Param.builder().name("param2").value("value").build());
        ExecuteQueryStatement statement = new ExecuteQueryStatement(query, params);

        when(clusterOperator.getCluster()).thenReturn(cluster);

        statement.execute(clusterOperator);

        verify(cluster).query(eq(query), any());
    }

}