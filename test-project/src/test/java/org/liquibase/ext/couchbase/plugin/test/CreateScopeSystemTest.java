package org.liquibase.ext.couchbase.plugin.test;

import com.couchbase.client.java.Bucket;
import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.plugin.common.ContainerizedTest;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createMavenPluginContainer;

public class CreateScopeSystemTest extends ContainerizedTest {

    private static final Bucket bucket = cluster.bucket(TEST_BUCKET);
    private static final BucketOperator bucketOperator = new BucketOperator(bucket);
    private static final String SCOPE_NAME = "createScopeName";
    private JavaMavenContainer javaMavenPluginContainer;


    @Test
    @SneakyThrows
    public void Should_create_new_scope() {
        javaMavenPluginContainer = createMavenPluginContainer(couchbaseContainer, "changelogs/create-scope.xml");

        javaMavenPluginContainer.start();
        while (javaMavenPluginContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(bucketOperator.hasScope(SCOPE_NAME));
        bucket.collections().dropScope(SCOPE_NAME);
    }

}
