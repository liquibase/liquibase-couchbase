package org.liquibase.ext.couchbase.plugin.test;

import com.couchbase.client.java.Bucket;
import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.plugin.common.ContainerizedTest;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;

import java.util.concurrent.TimeUnit;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createMavenPluginContainer;

public class CreateCollectionSystemTest extends ContainerizedTest {

    private static final Bucket bucket = cluster.bucket(TEST_BUCKET);
    private static final BucketOperator bucketOperator = new BucketOperator(bucket);
    private static final String COLLECTION_NAME = "createCollectionName";
    private JavaMavenContainer javaMavenPluginContainer;

    @Test
    @SneakyThrows
    public void Should_create_new_collection() {
        javaMavenPluginContainer = createMavenPluginContainer(couchbaseContainer, "changelogs/create-collection.xml");

        javaMavenPluginContainer.start();
        while (javaMavenPluginContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(bucketOperator.hasCollectionInScope(COLLECTION_NAME, TEST_SCOPE));
        bucket.collections().dropCollection(create(COLLECTION_NAME, TEST_SCOPE));
    }

}
