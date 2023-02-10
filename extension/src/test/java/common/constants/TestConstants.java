package common.constants;

import com.wdt.couchbase.Keyspace;
import org.testcontainers.utility.DockerImageName;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.wdt.couchbase.Keyspace.keyspace;

/**
 * Common constants for all tests
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {
    public static final String DEFAULT_SCOPE = "_default";
    public static final String DEFAULT_COLLECTION = "_default";
    public static final String TEST_SCOPE = "testScope";
    public static final String TEST_BUCKET = "testBucket";
    public static final String TEST_COLLECTION = "testCollection";
    public static final String TEST_COLLECTION_2 = "testCollection2";
    public static final String TEST_ID = "id";
    public static final String TEST_ID_2 = "id2";
    public static final String TEST_ID_3 = "id3";
    public static final String TEST_DOCUMENT = "{\"key\":\"value\"}";
    public static final String TEST_DOCUMENT_2 = "{\"key2\":\"value2\"}";
    public static final String TEST_DOCUMENT_3 = "{\"name\":\"user\", \"type\":\"customer\"}";
    public static final String PROPERTY_FILE_NAME = "src/test/resources/test.properties";
    public static final String INDEX = "testIndex";
    public static final String COMPOUND_INDEX = "testCompoundIndex";
    public static final String FIELD_1 = "name";
    public static final String FIELD_2 = "type";
    public static final String MANUALLY_CREATED_INDEX = "manually_created_index";
    public static final String TEST_CONTENT = "{ \"name\":\"user\", \"type\":\"customer\" }";

    public static final Keyspace TEST_KEYSPACE = keyspace(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
    public static final DockerImageName CB_IMAGE_NAME = DockerImageName.parse("couchbase/server");
}
