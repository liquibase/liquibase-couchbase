package common.constants;

import com.couchbase.client.java.json.JsonObject;

import org.testcontainers.utility.DockerImageName;

import liquibase.ext.couchbase.types.Keyspace;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;

/**
 * Common constants for all tests
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {
    public static final String DEFAULT_SCOPE = "_default";
    public static final String DEFAULT_COLLECTION = "_default";
    public static final String TRAVELS_BUCKET = "travels-bucket";
    public static final String TEST_SCOPE = "testScope";
    public static final String TEST_SCOPE_SQL = "sqlScope";
    public static final String TEST_BUCKET = "testBucket";
    public static final String TEST_COLLECTION = "testCollection";
    public static final String TEST_COLLECTION_2 = "testCollection2";
    public static final String TEST_COLLECTION_SQL = "sqlCollection";
    public static final String TEST_ID = "id";
    public static final JsonObject TEST_DOCUMENT = JsonObject.create().put("key", "value");
    public static final JsonObject TEST_DOCUMENT_2 = JsonObject.create().put("key2", "value2");
    public static final JsonObject TEST_DOCUMENT_3 = JsonObject.create().put("name", "user").put("type", "customer");
    public static final String PROPERTY_FILE_NAME = "src/test/resources/test.properties";
    public static final String INDEX = "testIndex";
    public static final String COMPOUND_INDEX = "testCompoundIndex";
    public static final String MANUALLY_CREATED_INDEX = "manually_created_index";

    public static final Keyspace TEST_KEYSPACE = keyspace(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
    public static final DockerImageName CB_IMAGE_NAME = DockerImageName.parse("couchbase/server");
    public static final String CREATE_BUCKET_TEST_NAME = "createBucketTest";
    public static final String CREATE_BUCKET_SYSTEM_TEST_NAME = "createBucketSystemTest";
    public static final Duration CLUSTER_READY_TIMEOUT = Duration.ofSeconds(10);
    public static final String NEW_TEST_BUCKET = "newTestBucket";
    public static final String UPDATE_TEST_BUCKET = "updateBucketTest";
}
