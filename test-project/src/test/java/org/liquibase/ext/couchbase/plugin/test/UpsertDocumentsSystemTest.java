package org.liquibase.ext.couchbase.plugin.test;

import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.operator.CollectionOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.plugin.common.ContainerizedTest;
import org.liquibase.ext.couchbase.plugin.containers.JavaMavenContainer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.plugin.common.TestContainerInitializer.createMavenPluginContainer;

public class UpsertDocumentsSystemTest extends ContainerizedTest {

    private static final Collection collection = cluster.bucket(TEST_BUCKET).scope(TEST_SCOPE).collection(TEST_COLLECTION);
    private static final CollectionOperator collectionOperator = new CollectionOperator(collection);
    private JavaMavenContainer javaMavenPluginContainer;

    @Test
    @SneakyThrows
    public void Should_upsert_new_document() {
        javaMavenPluginContainer = createMavenPluginContainer(couchbaseContainer, "changelogs/upsert-documents.xml");

        javaMavenPluginContainer.start();
        while (javaMavenPluginContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(collectionOperator.docExists("upsertId1"));
        collection.remove("upsertId1");
    }
}
