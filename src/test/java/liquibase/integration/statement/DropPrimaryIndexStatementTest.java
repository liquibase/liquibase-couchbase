package liquibase.integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.exception.DatabaseException;
import liquibase.ext.database.CouchbaseClientDriver;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.DropPrimaryIndexStatement;
import liquibase.integration.CouchbaseContainerizedTest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.Properties;


class DropPrimaryIndexStatementTest extends CouchbaseContainerizedTest {

    private static final String TEST_ID = "id1";
    private static final String TEST_CONTENT = "{ \"name\":\"user\", \"type\":\"customer\" }";
    private static final String TEST_COLLECTION = "testCollection";
    private static final String TEST_SCOPE = "testScope";
    private final String bucketName = "testBucket";
    private Cluster cluster;
    private Bucket bucket;
    private CouchbaseConnection connection;

    @BeforeEach
    void localSetUp() throws DatabaseException {
        CouchbaseClientDriver driver = new CouchbaseClientDriver();
        connection = new CouchbaseConnection();
        Properties properties = new Properties();
        properties.put("user", container.getUsername());
        properties.put("password", container.getPassword());
        connection.open(container.getConnectionString(), driver, properties);
        cluster = connection.getCluster();
        cluster.waitUntilReady(Duration.ofSeconds(20));
        cluster.buckets().createBucket(BucketSettings.create(bucketName));
        cluster.waitUntilReady(Duration.ofSeconds(20));
        bucket = cluster.bucket(bucketName);
        bucket.waitUntilReady(Duration.ofSeconds(20));
        bucket.defaultCollection().insert(TEST_ID, TEST_CONTENT);
        bucket.waitUntilReady(Duration.ofSeconds(20));
    }

    @AfterEach
    void localTearDown() throws DatabaseException {
        cluster.buckets().dropBucket(bucketName);
        connection.close();
    }

    @Test
    @DisplayName("Should drop the primary index if exists")
    void shouldDropPrimaryIndex() {
        cluster.queryIndexes().createPrimaryIndex(bucketName);
        cluster.waitUntilReady(Duration.ofSeconds(20));
        Assertions.assertEquals(1, cluster.queryIndexes().getAllIndexes(bucketName).size());

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(bucketName, null, null);
        statement.execute(connection);

        Assertions.assertTrue(CollectionUtils.isEmpty(cluster.queryIndexes().getAllIndexes(bucketName)));
    }

    @Test
    @DisplayName("Should drop primary index for specific keyspace")
    void shouldDropPrimaryIndexForKeyspace() {
        bucket.collections().createScope(TEST_SCOPE);
        bucket.collections().createCollection(CollectionSpec.create(TEST_COLLECTION, TEST_SCOPE));
        CreatePrimaryQueryIndexOptions options = CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions()
                .scopeName(TEST_SCOPE)
                .collectionName(TEST_COLLECTION);
        cluster.queryIndexes().createPrimaryIndex(bucketName, options);
        cluster.waitUntilReady(Duration.ofSeconds(20));
        Assertions.assertEquals(1, cluster.queryIndexes().getAllIndexes(bucketName).size());

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(bucketName, TEST_COLLECTION, TEST_SCOPE);
        statement.execute(connection);

        Assertions.assertTrue(CollectionUtils.isEmpty(cluster.queryIndexes().getAllIndexes(bucketName)));
    }
}