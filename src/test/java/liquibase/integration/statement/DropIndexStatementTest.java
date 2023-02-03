package liquibase.integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.exception.DatabaseException;
import liquibase.ext.database.CouchbaseClientDriver;
import liquibase.ext.database.CouchbaseConnection;
import liquibase.ext.statement.DropIndexStatement;
import liquibase.integration.CouchbaseContainerizedTest;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


class DropIndexStatementTest extends CouchbaseContainerizedTest {
    private static final String TEST_ID = "id1";
    private static final String TEST_CONTENT = "{ \"name\":\"user\", \"type\":\"customer\" }";
    private static final String TEST_COLLECTION = "testCollection";
    private static final String TEST_SCOPE = "testScope";
    private final String bucketName = "testBucket";
    private final String indexName = "testIndex";
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
    @DisplayName("Should drop the index if exists")
    void shouldDropIndex() {
        cluster.queryIndexes().createIndex(bucketName, indexName, Collections.singletonList("name"));
        cluster.waitUntilReady(Duration.ofSeconds(20));
        Assertions.assertEquals(1, cluster.queryIndexes().getAllIndexes(bucketName).size());

        DropIndexStatement statement = new DropIndexStatement(indexName, bucketName, null, null);
        statement.execute(connection);

        Assertions.assertTrue(CollectionUtils.isEmpty(cluster.queryIndexes().getAllIndexes(bucketName)));
    }

    @Test
    @DisplayName("Should drop index for specific keyspace")
    void shouldDropIndexForSpecificKeyspace() {
        bucket.collections().createScope(TEST_SCOPE);
        bucket.collections().createCollection(CollectionSpec.create(TEST_COLLECTION, TEST_SCOPE));
        CreateQueryIndexOptions options = CreateQueryIndexOptions.createQueryIndexOptions().collectionName(TEST_COLLECTION).scopeName(TEST_SCOPE);
        cluster.queryIndexes().createIndex(bucketName, indexName, Collections.singletonList("name"), options);
        cluster.waitUntilReady(Duration.ofSeconds(20));

        Assertions.assertEquals(1, cluster.queryIndexes().getAllIndexes(bucketName).size());
        DropIndexStatement statement = new DropIndexStatement(indexName, bucketName, TEST_COLLECTION, TEST_SCOPE);
        statement.execute(connection);

        Assertions.assertTrue(CollectionUtils.isEmpty(cluster.queryIndexes().getAllIndexes(bucketName)));
    }

}