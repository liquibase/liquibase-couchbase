package liquibase.integration.statement;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.DocumentExistsByKeyStatement;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DocumentExistsByKeyStatementIT extends CouchbaseContainerizedTest {

    private static Cluster cluster;
    private static CouchbaseConnection connection;


    private static final String scopeName = "testScope";
    private static final String collectionName = "testCollection";

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
    void Should_return_true_when_document_exists() {
        String key = "key";
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        cluster.bucket(TEST_BUCKET).collections().createCollection(CollectionSpec.create(collectionName, scopeName));
        cluster.bucket(TEST_BUCKET).scope(scopeName).collection(collectionName).insert(key, "testObject");
        DocumentExistsByKeyStatement statement =
                new DocumentExistsByKeyStatement(TEST_BUCKET, scopeName, collectionName, key);

        boolean returnedResult = statement.isCDocumentExists(connection);

        assertTrue(returnedResult);

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }

    @Test
    void Should_return_false_when_document_doesnt_exists() {
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        cluster.bucket(TEST_BUCKET).collections().createCollection(CollectionSpec.create(collectionName, scopeName));
        DocumentExistsByKeyStatement statement =
                new DocumentExistsByKeyStatement(TEST_BUCKET, scopeName, collectionName, "notExistedKey");

        boolean returnedResult = statement.isCDocumentExists(connection);

        assertFalse(returnedResult);

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }
}
