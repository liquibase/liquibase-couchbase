package common.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.utility.DockerImageName;

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
    public static final String TEST_DOCUMENT = "{ \"key\":\"value\"}";
    public static final String TEST_DOCUMENT_2 = "{ \"key2\":\"value2\"}";
    public static final String PROPERTY_FILE_NAME = "src/test/resources/test.properties";

    public static final DockerImageName CB_IMAGE_NAME = DockerImageName.parse("couchbase/server");
}
