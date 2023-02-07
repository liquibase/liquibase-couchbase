package common;

import com.couchbase.client.java.Cluster;

import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;

import java.time.Duration;

import liquibase.ext.couchbase.database.ConnectionData;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import static common.constants.TestConstants.CB_IMAGE_NAME;
import static common.constants.TestConstants.TEST_BUCKET;

/**
 * Basis for Couchbase interacting tests
 * Data will not be cleared automatically, it's your care to clean up
 */
public abstract class CouchbaseContainerizedTest {
    protected static final Cluster cluster;
    protected static final CouchbaseContainer container;
    protected static final CouchbaseLiquibaseDatabase database;

    static {
        container = createContainer();
        container.start();
        database = createDatabase();
        cluster = database.getConnection().getCluster();
    }

    private static CouchbaseLiquibaseDatabase createDatabase() {
        return new CouchbaseLiquibaseDatabase(new ConnectionData(
                container.getUsername(),
                container.getPassword(),
                container.getConnectionString()
        ));
    }

    private static CouchbaseContainer createContainer() {
        String cbVersion = PropertyProvider.getProperty("couchbase.version");
        BucketDefinition bucketDef = new BucketDefinition(TEST_BUCKET).withPrimaryIndex(false);

        return new CouchbaseContainer(CB_IMAGE_NAME.withTag(cbVersion))
                .withBucket(bucketDef)
                .withServiceQuota(CouchbaseService.KV, 512)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .waitingFor(Wait.forHealthcheck());
    }


}
