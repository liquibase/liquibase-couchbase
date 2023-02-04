package liquibase.integration;

import com.couchbase.client.java.Cluster;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import liquibase.ext.database.ConnectionData;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;

/**
 * Basis for Couchbase interacting tests
 * Data will not be cleared automatically, it's your care to clean up
 */
public abstract class CouchbaseContainerizedTest {
    public static final String TEST_SCOPE = "testScope";
    public static final String TEST_BUCKET = "testBucket";
    public static final String TEST_COLLECTION = "testCollection";

    protected static final String COUCHBASE_VERSION = "7.1.3";
    protected static final DockerImageName IMAGE_NAME = DockerImageName.parse("couchbase/server");
    protected static final CouchbaseContainer container;

    public static final Cluster cluster;
    public static final CouchbaseLiquibaseDatabase database;

    static {
        BucketDefinition bucketDefinition =
                new BucketDefinition(TEST_BUCKET).withPrimaryIndex(false);
        container = new CouchbaseContainer(IMAGE_NAME.withTag(COUCHBASE_VERSION))
                .withBucket(bucketDefinition)
                .withServiceQuota(CouchbaseService.KV, 512)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .waitingFor(Wait.forHealthcheck());
        container.start();
        database = new CouchbaseLiquibaseDatabase(new ConnectionData(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        ));
        cluster = database.getConnection().getCluster();
    }


}
