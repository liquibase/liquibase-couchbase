package common.constants;

import org.testcontainers.utility.DockerImageName;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Common constants for all tests
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestConstants {
    public static final String TEST_SCOPE = "testScope";
    public static final String TEST_BUCKET = "testBucket";
    public static final String TEST_COLLECTION = "testCollection";
    public static final String PROPERTY_FILE_NAME = "src/test/resources/test.properties";

    public static final DockerImageName CB_IMAGE_NAME = DockerImageName.parse("couchbase/server");
}
