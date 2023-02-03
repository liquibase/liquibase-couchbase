package liquibase.common.connection;

import com.couchbase.client.java.Cluster;

import org.testcontainers.couchbase.CouchbaseContainer;

public class TestClusterInitializer {

    public static Cluster connect(CouchbaseContainer container) {
        return Cluster.connect(
                container.getConnectionString(),
                container.getUsername(),
                container.getPassword()
        );
    }
}
