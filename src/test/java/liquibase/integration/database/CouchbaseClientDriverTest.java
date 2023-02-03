package liquibase.integration.database;

import static com.couchbase.client.java.ClusterOptions.clusterOptions;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import liquibase.exception.DatabaseException;
import liquibase.ext.database.CouchbaseClientDriver;
import liquibase.integration.CouchbaseContainerizedTest;


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