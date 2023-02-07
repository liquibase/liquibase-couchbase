package integration.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import common.CouchbaseContainerizedTest;
import liquibase.exception.DatabaseException;
import liquibase.ext.couchbase.database.CouchbaseClientDriver;
import static com.couchbase.client.java.ClusterOptions.clusterOptions;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class CouchbaseClientDriverTest extends CouchbaseContainerizedTest {

    public static CouchbaseClientDriver driver;

    @BeforeAll
    static void setUpLocal() {
        driver = new CouchbaseClientDriver();
    }

    @Test
    void Should_connect_to_cluster_using_driver() throws DatabaseException {
        ClusterOptions credentials = clusterOptions(container.getUsername(), container.getPassword());

        Cluster cluster = driver.connect(container.getConnectionString(), credentials);

        assertNotNull(cluster);
    }
}