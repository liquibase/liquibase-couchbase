package org.liquibase.ext.couchbase.starter.test;

import com.couchbase.client.java.Collection;
import org.junit.Test;
import org.liquibase.ext.couchbase.starter.common.SpringBootCouchbaseContainerizedTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class InsertDocumentsSystemTest extends SpringBootCouchbaseContainerizedTest {

    private static final Collection collection = cluster.bucket(TEST_BUCKET).scope(TEST_SCOPE).collection(TEST_COLLECTION);

    @DynamicPropertySource
    static void overrideUrlProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.liquibase.couchbase.change-log", () -> "classpath:/testdb/changelog/insert-document.xml");
    }

    @Test
    public void Should_insert_new_document() {
        assertTrue(collection.exists("insertId1").exists());
        collection.remove("insertId1");
    }
}
