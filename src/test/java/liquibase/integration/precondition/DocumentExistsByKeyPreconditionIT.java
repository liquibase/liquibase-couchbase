package liquibase.integration.precondition;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.wdt.couchbase.exception.DocumentNotExistsPreconditionException;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.precondition.DocumentExistsByKeyPrecondition;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DocumentExistsByKeyPreconditionIT extends CouchbaseContainerizedTest {

    private static Cluster cluster;
    private static CouchbaseLiquibaseDatabase database;

    private static final String scopeName = "testScope";
    private static final String collectionName = "testCollection";
    private static final DocumentExistsByKeyPrecondition precondition = new DocumentExistsByKeyPrecondition();

    @BeforeEach
    void setUpLocal() {
        cluster = TestClusterInitializer.connect(container);
        precondition.setBucketName(TEST_BUCKET);
        precondition.setScopeName(scopeName);
        precondition.setCollectionName(collectionName);
        database = new TestCouchbaseDatabase(container);
    }

    @AfterAll
    @SneakyThrows
    static void tearDownLocal() {
        database.close();
        cluster.close();
    }

    @Test
    void Should_not_throws_when_document_exists() {
        String key = "key";
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        cluster.bucket(TEST_BUCKET).collections().createCollection(CollectionSpec.create(collectionName, scopeName));
        cluster.bucket(TEST_BUCKET).scope(scopeName).collection(collectionName).insert(key, "testObject");

        precondition.setKey(key);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }

    @Test
    void Should_throw_exception_when_document_doesnt_exists() {
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        cluster.bucket(TEST_BUCKET).collections().createCollection(CollectionSpec.create(collectionName, scopeName));

        precondition.setKey("notExistedKey");

        assertThatExceptionOfType(DocumentNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Key notExistedKey does not exist in bucket test in scope testScope and collection testCollection");

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }
}
