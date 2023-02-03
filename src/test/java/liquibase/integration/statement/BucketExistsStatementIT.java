package liquibase.integration.statement;

import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.BucketExistsStatement;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BucketExistsStatementIT extends CouchbaseContainerizedTest {

    private static CouchbaseConnection connection;

    @BeforeAll
    static void setUpLocal() {
        connection = new TestCouchbaseDatabase(container).getConnection();
    }

    @AfterAll
    @SneakyThrows
    static void tearDownLocal() {
        connection.close();
    }

    @Test
    void Should_return_true_when_bucket_exists() {
        BucketExistsStatement statement = new BucketExistsStatement(TEST_BUCKET);

        boolean returnedResult = statement.isBucketExists(connection);

        assertTrue(returnedResult);
    }

    @Test
    void Should_return_false_when_bucket_doesnt_exists() {
        BucketExistsStatement statement = new BucketExistsStatement("someNotCreatedBucket");

        boolean returnedResult = statement.isBucketExists(connection);

        assertFalse(returnedResult);
    }
}
