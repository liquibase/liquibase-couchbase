package org.liquibase.ext.couchbase.starter.test;

import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.time.Duration;

import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_LOCK_COLLECTION_NAME;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.IS_REACTIVE_TRANSACTIONS;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.LOCK_TTL;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.LOCK_TTL_PROLONGATION;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.MUTATE_IN_TIMEOUT;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.REACTIVE_TRANSACTION_PARALLEL_THREADS;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.SERVICE_BUCKET_NAME;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.TRANSACTION_TIMEOUT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This test class temporary uses liquibase-couchbase.properties file from src/main/resources folder, not from test resources. Because
 * otherwise there will be stackoverflow error. TODO fix it
 */
public class CustomSettingsSystemTest extends SpringBootCouchbaseContainerizedTest {

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/custom-settings-mock-test.xml");
    }

    @Test
    public void Should_set_custom_settings() {
        assertEquals(Duration.ofSeconds(61), CouchbaseLiquibaseConfiguration.getChangelogRecheckTime());
        assertEquals(Duration.ofSeconds(46), CouchbaseLiquibaseConfiguration.getChangelogWaitTime());
        assertEquals(Duration.ofSeconds(16), LOCK_TTL.getCurrentValue());
        assertEquals(Duration.ofSeconds(11), LOCK_TTL_PROLONGATION.getCurrentValue());
        assertEquals(Duration.ofSeconds(26), TRANSACTION_TIMEOUT.getCurrentValue());
        assertEquals(Duration.ofSeconds(26), MUTATE_IN_TIMEOUT.getCurrentValue());
        assertEquals(9, REACTIVE_TRANSACTION_PARALLEL_THREADS.getCurrentValue());
        assertTrue(IS_REACTIVE_TRANSACTIONS.getCurrentValue());
        assertEquals("customLockCollectionName", CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue());
        assertEquals("customBucketName", SERVICE_BUCKET_NAME.getCurrentValue());
    }

}
