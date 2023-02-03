package liquibase.integration.precondition;

import com.couchbase.client.java.Cluster;
import com.wdt.couchbase.exception.ScopeNotExistsPreconditionException;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.precondition.ScopeExistsPrecondition;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ScopeExistsPreconditionIT extends CouchbaseContainerizedTest {

    private static Cluster cluster;
    private static CouchbaseLiquibaseDatabase database;

    private static final String scopeName = "testScope";
    private static final ScopeExistsPrecondition precondition = new ScopeExistsPrecondition();

    @BeforeEach
    void setUpLocal() {
        cluster = TestClusterInitializer.connect(container);
        precondition.setBucketName(TEST_BUCKET);
        database = new TestCouchbaseDatabase(container);
    }

    @AfterAll
    @SneakyThrows
    static void tearDownLocal() {
        database.close();
        cluster.close();
    }

    @Test
    void Should_not_throws_when_scope_exists() {
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        precondition.setScopeName(scopeName);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }

    @Test
    void Should_throw_exception_when_scope_doesnt_exists() {
        precondition.setScopeName("notExistedScope");

        assertThatExceptionOfType(ScopeNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Scope notExistedScope does not exist in bucket test");
    }
}
