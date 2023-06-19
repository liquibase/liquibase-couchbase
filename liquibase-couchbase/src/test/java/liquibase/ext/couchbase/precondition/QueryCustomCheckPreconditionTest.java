package liquibase.ext.couchbase.precondition;

import com.couchbase.client.core.api.query.CoreQueryResult;
import com.couchbase.client.core.classic.query.ClassicCoreQueryResult;
import com.couchbase.client.core.deps.com.google.common.collect.Lists;
import com.couchbase.client.core.msg.query.QueryChunkRow;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.codec.JacksonJsonSerializer;
import com.couchbase.client.java.query.QueryResult;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.SqlCheckPreconditionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class QueryCustomCheckPreconditionTest {

    private final Database database = mock(Database.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);

    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
    }

    @Test
    @SneakyThrows
    void Should_pass_when_result_is_expected() {
        String query = "\"a\"=\"b\"";
        QueryCustomCheckPrecondition precondition = new QueryCustomCheckPrecondition();
        precondition.setExpectedResultJson("[{\"abcd\":\"efgh\"}]");
        precondition.setQuery(query);
        CoreQueryResult queryResult = new ClassicCoreQueryResult(
                null, Lists.newArrayList(new QueryChunkRow("{\"abcd\":\"efgh\"}".getBytes())),
                null, null
        );

        when(cluster.query(query)).thenReturn(new QueryResult(queryResult, JacksonJsonSerializer.create()));
        precondition.check(database, null, null, null);
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_result_is_unexpected() {
        String query = "abcd";
        QueryCustomCheckPrecondition precondition = new QueryCustomCheckPrecondition();
        precondition.setExpectedResultJson("[{\"abcd\":\"efgh\"}]");
        precondition.setQuery(query);
        CoreQueryResult queryResult = new ClassicCoreQueryResult(
                null, Lists.newArrayList(new QueryChunkRow("{\"abcd\":\"efghk\"}".getBytes())),
                null, null
        );

        when(cluster.query(query)).thenReturn(new QueryResult(queryResult, JacksonJsonSerializer.create()));

        assertThatExceptionOfType(SqlCheckPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Result of [%s] query is differ then expected[%s]", precondition.getQuery(),
                        precondition.getExpectedResultJson());
    }
}