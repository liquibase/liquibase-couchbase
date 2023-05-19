package org.liquibase.ext.couchbase.cli.test.collection;

import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.cli.test.common.CLIContainerizedTest;
import org.liquibase.ext.couchbase.cli.test.containers.LiquibaseContainer;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.liquibase.ext.couchbase.cli.test.common.TestContainerInitializer.createLiquibaseContainer;
import static org.liquibase.ext.couchbase.cli.test.common.TestContainerInitializer.getPathOfShadeJar;

class CollectionCLITest extends CLIContainerizedTest {
    LiquibaseContainer liquibaseContainer;

    @AfterEach
    void cleanUpEach() {
        liquibaseContainer.stop();
    }

    @Test
    @SneakyThrows
    void Should_create_collection() {
        liquibaseContainer = createLiquibaseContainer(couchbaseContainer,
                "collection/changelog.create-collection.test.xml", getPathOfShadeJar());

        liquibaseContainer.start();
        while (liquibaseContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(clusterOperator.getBucketOperator("createBucketTest").hasCollectionInScope(TEST_COLLECTION, TEST_SCOPE));
    }

    @Test
    @SneakyThrows
    void Should_drop_collection() {
        clusterOperator.createBucket("dropCollectionTest");
        BucketOperator bucketOperator = clusterOperator.getBucketOperator("dropCollectionTest");
        bucketOperator.createScope(TEST_SCOPE);
        bucketOperator.createCollection(TEST_COLLECTION, TEST_SCOPE);

        liquibaseContainer = createLiquibaseContainer(couchbaseContainer,
                "collection/changelog.drop-collection.test.xml", getPathOfShadeJar());
        liquibaseContainer.start();
        while (liquibaseContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertFalse(bucketOperator.hasCollectionInScope(TEST_COLLECTION, TEST_SCOPE));
    }

    @Test
    @SneakyThrows
    void Should_upsert_document_to_collection() {
        BucketOperator bucketOperator = clusterOperator.getBucketOperator("testBucket");
        bucketOperator.createScope(TEST_SCOPE);
        bucketOperator.createCollection(TEST_COLLECTION, TEST_SCOPE);

        liquibaseContainer = createLiquibaseContainer(couchbaseContainer,
                "collection/changelog.collection-upsert-doc.test.xml", getPathOfShadeJar());
        liquibaseContainer.start();
        while (liquibaseContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE).docExists("upsertId1"));
    }

}