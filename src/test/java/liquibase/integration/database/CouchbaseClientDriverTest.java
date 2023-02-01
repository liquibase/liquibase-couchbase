package liquibase.integration.database;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.ClusterOptions;
import liquibase.exception.DatabaseException;
import liquibase.ext.database.CouchbaseClientDriver;
import liquibase.integration.CouchbaseContainerizedTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


class CouchbaseClientDriverTest extends CouchbaseContainerizedTest {

    public static CouchbaseClientDriver driver;

    @BeforeAll
    static void setUpLocal() {
        driver = new CouchbaseClientDriver();
    }

    @Test
    void shouldConnect() throws DatabaseException {
        ClusterOptions credentials = ClusterOptions.clusterOptions(container.getUsername(), container.getPassword());
        Cluster clusterByDriver = driver.connect(container.getConnectionString(), credentials);
        Assertions.assertNotNull(clusterByDriver);
    }
}