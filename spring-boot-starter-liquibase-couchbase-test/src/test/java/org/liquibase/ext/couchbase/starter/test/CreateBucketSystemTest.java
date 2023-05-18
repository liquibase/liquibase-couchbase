package org.liquibase.ext.couchbase.starter.test;

import org.junit.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateBucketSystemTest extends SpringBootCouchbaseContainerizedTest {

    private static final String BUCKET_NAME = "createBucketName";

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/create-bucket.xml");
    }

    @Test
    public void Should_create_new_bucket() {
        assertTrue(isBucketExists());
        cluster.buckets().dropBucket(BUCKET_NAME);
    }

    private static boolean isBucketExists() {
        return cluster.buckets().getBucket(BUCKET_NAME) != null;
    }
}
