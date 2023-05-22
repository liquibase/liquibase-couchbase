package org.liquibase.ext.couchbase.starter.test;

import liquibase.ext.couchbase.operator.ClusterOperator;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateBucketSystemTest extends SpringBootCouchbaseContainerizedTest {

    private static final String BUCKET_NAME = "createBucketName";
    private static final ClusterOperator clusterOperator = new ClusterOperator(cluster);

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/create-bucket.xml");
    }

    @Test
    public void Should_create_new_bucket() {
        assertTrue(clusterOperator.isBucketExists(BUCKET_NAME));
        cluster.buckets().dropBucket(BUCKET_NAME);
    }

}
