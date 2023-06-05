package org.liquibase.ext.couchbase.starter.common;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;

/**
 * Basis for Couchbase interacting tests Data will not be cleared automatically, it's your care to clean up
 */
@SpringBootTest
public abstract class SpringBootCouchbaseContainerizedTest {

    private static final DockerImageName CB_IMAGE_NAME = DockerImageName.parse("couchbase/server");
    protected static final String TEST_BUCKET = "testBucket";
    protected static final String TEST_SCOPE = "testScope";
    protected static final String TEST_COLLECTION = "testCollection";
    protected static final Cluster cluster;
    protected static final CouchbaseContainer container;
    protected static final CouchbaseLiquibaseDatabase database;

    static {
        container = createContainer(TEST_BUCKET);
        container.start();
        database = createDatabase(container);
        cluster = database.getConnection().getCluster();
        Bucket bucket = cluster.bucket(TEST_BUCKET);
        bucket.collections().createScope(TEST_SCOPE);
        bucket.collections().createCollection(create(TEST_COLLECTION, TEST_SCOPE));
    }

    private static CouchbaseLiquibaseDatabase createDatabase(CouchbaseContainer container) {
        return new CouchbaseLiquibaseDatabase(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        );
    }

    private static CouchbaseContainer createContainer(String testBucket) {
        String cbVersion = TestPropertyProvider.getProperty("couchbase.version");
        BucketDefinition bucketDef = new BucketDefinition(testBucket).withPrimaryIndex(false);

        return new CouchbaseContainer(CB_IMAGE_NAME.withTag(cbVersion))
                .withBucket(bucketDef)
                .withServiceQuota(CouchbaseService.KV, 512)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .waitingFor(Wait.forHealthcheck());
    }

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.url", container::getConnectionString);
    }

}
