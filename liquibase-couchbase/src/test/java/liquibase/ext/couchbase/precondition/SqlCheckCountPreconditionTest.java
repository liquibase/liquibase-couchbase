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
import liquibase.ext.couchbase.exception.precondition.SqlCheckCountPreconditionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SqlCheckCountPreconditionTest {

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
    void Should_pass_when_count_is_expected() {
        String query = "\"a\"=\"b\"";
        SqlCheckCountPrecondition precondition = new SqlCheckCountPrecondition(1, query);
        CoreQueryResult queryResult = new ClassicCoreQueryResult(
                null, Lists.newArrayList(new QueryChunkRow("{\"count\":1}".getBytes())),
                null, null
        );

        when(cluster.query(query)).thenReturn(new QueryResult(queryResult, JacksonJsonSerializer.create()));
        precondition.check(database, null, null, null);
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_count_is_unexpected() {
        String query = "testQuery";
        SqlCheckCountPrecondition precondition = new SqlCheckCountPrecondition();
        precondition.setCount(5);
        precondition.setQuery(query);
        CoreQueryResult queryResult = new ClassicCoreQueryResult(
                null, Lists.newArrayList(new QueryChunkRow("{\"count\":1}".getBytes())),
                null, null
        );
        when(cluster.query(query)).thenReturn(new QueryResult(queryResult, JacksonJsonSerializer.create()));

        assertThatExceptionOfType(SqlCheckCountPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Sql precondition query [%s] result is different then expected count [%d]", precondition.getQuery(),
                        precondition.getCount());

    }
}