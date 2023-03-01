package common;

import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import lombok.extern.slf4j.Slf4j;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;

/**
 * Singleton Creates "testScope" and "testCollection" before all tests
 */
@Slf4j
public class ConstantScopeTestCase extends CouchbaseContainerizedTest {
    protected static final TestClusterOperator clusterOperator = new TestClusterOperator(cluster);
    protected static final TestBucketOperator bucketOperator = new TestBucketOperator(cluster);

    static {
        bucketOperator.createDefaultTestScope();
        bucketOperator.createDefaultTestCollection();
    }
}
