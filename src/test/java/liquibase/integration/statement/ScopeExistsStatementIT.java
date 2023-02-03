package liquibase.integration.statement;

import com.couchbase.client.java.Cluster;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.ScopeExistsStatement;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScopeExistsStatementIT extends CouchbaseContainerizedTest {

    private static Cluster cluster;
    private static CouchbaseConnection connection;

    @BeforeAll
    static void setUpLocal() {
        cluster = TestClusterInitializer.connect(container);
        connection = new TestCouchbaseDatabase(container).getConnection();
    }

    @AfterAll
    @SneakyThrows
    static void tearDownLocal() {
        connection.close();
        cluster.close();
    }

    @Test
    void Should_return_true_when_scope_exists() {
        String scopeName = "testScope";
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        ScopeExistsStatement statement = new ScopeExistsStatement(TEST_BUCKET, scopeName);

        boolean returnedResult = statement.isScopeExists(connection);

        assertTrue(returnedResult);

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }

    @Test
    void Should_return_false_when_scope_doesnt_exists() {
        ScopeExistsStatement statement = new ScopeExistsStatement(TEST_BUCKET, "notCreatedScope");

        boolean returnedResult = statement.isScopeExists(connection);

        assertFalse(returnedResult);
    }
}
