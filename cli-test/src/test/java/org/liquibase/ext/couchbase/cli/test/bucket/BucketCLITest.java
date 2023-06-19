package org.liquibase.ext.couchbase.cli.test.bucket;

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

class BucketCLITest extends CLIContainerizedTest {
    LiquibaseContainer liquibaseContainer;

    @AfterEach
    void cleanUpEach() {
        liquibaseContainer.stop();
    }

    @Test
    @SneakyThrows
    void Should_create_bucket_scope_collection() {
        liquibaseContainer = createLiquibaseContainer(couchbaseContainer,
                "bucket/changelog.create-bucket.test.xml", getPathOfShadeJar());

        liquibaseContainer.start();
        while (liquibaseContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertTrue(clusterOperator.isBucketExists("createBucketTest"));
    }


    @Test
    @SneakyThrows
    void Should_drop_bucket() {
        clusterOperator.createBucket("dropBucketTest");
        liquibaseContainer = createLiquibaseContainer(couchbaseContainer,
                "bucket/changelog.drop-bucket.test.xml", getPathOfShadeJar());

        liquibaseContainer.start();
        while (liquibaseContainer.isRunning()) {
            TimeUnit.SECONDS.sleep(5L);
        }

        assertFalse(clusterOperator.isBucketExists("dropBucketTest"));
    }


}