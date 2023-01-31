package liquibase.integration;

import com.couchbase.client.java.Cluster;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import lombok.SneakyThrows;

public abstract class CouchbaseContainerizedTest {
    private static final String couchbaseVersion = "7.1.3";
    private static final DockerImageName IMAGE_NAME = DockerImageName.parse("couchbase/server");

    protected Cluster cluster;
    protected CouchbaseContainer container;

    @SneakyThrows
    @BeforeEach
    void setUp() {
        BucketDefinition bucketDefinition = new BucketDefinition("test");
        container = new CouchbaseContainer(IMAGE_NAME.withTag(couchbaseVersion))
                .withBucket(bucketDefinition)
                .withStartupTimeout(Duration.ofMinutes(2L))
                .waitingFor(Wait.forHealthcheck());
        container.start();
        cluster = Cluster.connect(
                container.getConnectionString(),
                container.getUsername(),
                container.getPassword()
        );
    }

    @AfterEach
    void tearDown() {
        cluster.close();
    }
}
