package liquibase.integration.statement;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.CollectionExistsStatement;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CollectionExistsStatementIT extends CouchbaseContainerizedTest {

    private static Cluster cluster;
    private static CouchbaseConnection connection;

    private static final String scopeName = "testScope";

    @BeforeEach
    void setUpLocal() {
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
    void Should_return_true_when_collection_exists() {
        String collectionName = "testCollection";
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        cluster.bucket(TEST_BUCKET).collections().createCollection(CollectionSpec.create(collectionName, scopeName));
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, scopeName, collectionName);

        boolean returnedResult = statement.isCollectionExists(connection);

        assertTrue(returnedResult);

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }

    @Test
    void Should_return_false_when_collection_doesnt_exists() {
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, scopeName, "notCreatedCollection");

        boolean returnedResult = statement.isCollectionExists(connection);

        assertFalse(returnedResult);

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }
}
