package org.liquibase.ext.couchbase.starter.test;

import com.couchbase.client.java.Bucket;
import liquibase.ext.couchbase.operator.BucketOperator;
import org.junit.jupiter.api.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateCollectionSystemTest extends SpringBootCouchbaseContainerizedTest {

    private static final Bucket bucket = cluster.bucket(TEST_BUCKET);
    private static final BucketOperator bucketOperator = new BucketOperator(bucket);
    private static final String COLLECTION_NAME = "createCollectionName";

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/create-collection.xml");
    }

    @Test
    public void Should_create_new_collection() {
        assertTrue(bucketOperator.hasCollectionInScope(COLLECTION_NAME, TEST_SCOPE));
        bucket.collections().dropCollection(create(COLLECTION_NAME, TEST_SCOPE));
    }

}
