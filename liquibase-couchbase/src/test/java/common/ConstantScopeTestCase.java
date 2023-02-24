package common;

import common.operators.TestBucketOperator;
import lombok.extern.slf4j.Slf4j;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;

/**
 * Singleton Creates "testScope" and "testCollection" before all tests
 */
@Slf4j
public class ConstantScopeTestCase extends CouchbaseContainerizedTest {
    protected static final TestBucketOperator bucketOperator = new TestBucketOperator(cluster);

    static {
        bucketOperator.createScope(TEST_SCOPE);
        bucketOperator.createCollection(TEST_COLLECTION, TEST_SCOPE);
    }
}
