package liquibase.integration.precondition;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.wdt.couchbase.exception.CollectionsNotExistsPreconditionException;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.precondition.CollectionExistsPrecondition;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CollectionExistsPreconditionIT extends CouchbaseContainerizedTest {

    private static Cluster cluster;
    private static CouchbaseLiquibaseDatabase database;

    private static final String scopeName = "testScope";
    private static final CollectionExistsPrecondition precondition = new CollectionExistsPrecondition();

    @BeforeAll
    static void setUpLocal() {
        cluster = TestClusterInitializer.connect(container);
        precondition.setBucketName(TEST_BUCKET);
        precondition.setScopeName(scopeName);
        database = new TestCouchbaseDatabase(container);}

    @AfterAll
    @SneakyThrows
    static void tearDownLocal() {
        database.close();
        cluster.close();
    }

    @Test
    void Should_not_throws_when_collection_exists() {
        String collectionName = "testCollection";
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        cluster.bucket(TEST_BUCKET).collections().createCollection(CollectionSpec.create(collectionName, scopeName));
        precondition.setCollectionName(collectionName);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }

    @Test
    void Should_throw_exception_when_collection_doesnt_exists() {
        cluster.bucket(TEST_BUCKET).collections().createScope(scopeName);
        precondition.setCollectionName("notCreatedCollection");

        assertThatExceptionOfType(CollectionsNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Collection notCreatedCollection does not exist in bucket test in scope testScope");

        cluster.bucket(TEST_BUCKET).collections().dropScope(scopeName);
    }
}
