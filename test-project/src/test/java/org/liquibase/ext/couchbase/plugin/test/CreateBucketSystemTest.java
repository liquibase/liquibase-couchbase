package org.liquibase.ext.couchbase.plugin.test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.plugin.common.ContainerizedTest;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createMavenPluginContainer;

class CreateBucketSystemTest extends ContainerizedTest {
    private static final String BUCKET_NAME = "createBucketName";
    private JavaMavenContainer javaMavenPluginContainer;

    @AfterEach
    void cleanUpEach() {
        javaMavenPluginContainer.stop();
    }

    @Test
    @SneakyThrows
    void Should_create_bucket() {
        javaMavenPluginContainer = createMavenPluginContainer(couchbaseContainer, "changelogs/create-bucket.xml");

        javaMavenPluginContainer.start();
        while (javaMavenPluginContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(clusterOperator.isBucketExists(BUCKET_NAME));
        cluster.buckets().dropBucket(BUCKET_NAME);
    }

}