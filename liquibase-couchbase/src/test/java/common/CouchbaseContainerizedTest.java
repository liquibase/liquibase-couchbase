package common;

import com.couchbase.client.java.Cluster;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.testcontainers.couchbase.CouchbaseContainer;

import static common.ContainerizedTestUtil.createContainer;
import static common.ContainerizedTestUtil.createDatabase;
import static common.constants.TestConstants.TEST_BUCKET;

/**
 * Basis for Couchbase interacting tests Data will not be cleared automatically, it's your care to clean up
 */
public abstract class CouchbaseContainerizedTest {
    protected static final Cluster cluster;
    protected static final CouchbaseContainer container;
    protected static final CouchbaseLiquibaseDatabase database;

    static {
        container = createContainer(TEST_BUCKET);
        container.start();
        database = createDatabase(container);
        cluster = database.getConnection().getCluster();
    }

}
