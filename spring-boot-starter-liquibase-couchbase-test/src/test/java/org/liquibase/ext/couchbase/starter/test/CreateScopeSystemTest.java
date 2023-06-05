package org.liquibase.ext.couchbase.starter.test;

import com.couchbase.client.java.Bucket;
import liquibase.ext.couchbase.operator.BucketOperator;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateScopeSystemTest extends SpringBootCouchbaseContainerizedTest {

    private static final Bucket bucket = cluster.bucket(TEST_BUCKET);
    private static final BucketOperator bucketOperator = new BucketOperator(bucket);
    private static final String SCOPE_NAME = "createScopeName";

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/create-scope.xml");
    }

    @Test
    public void Should_create_new_scope() {
        assertTrue(bucketOperator.hasScope(SCOPE_NAME));
        bucket.collections().dropScope(SCOPE_NAME);
    }

}
