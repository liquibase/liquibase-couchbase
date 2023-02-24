package common;

import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import lombok.extern.slf4j.Slf4j;

import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;

/**
 * Singleton Use existing testBucket, creates random scope and collection before all tests
 */
@Slf4j
public class RandomizedScopeTestCase extends CouchbaseContainerizedTest {
    protected static final TestClusterOperator clusterOperator = new TestClusterOperator(cluster);
    protected static final TestBucketOperator bucketOperator = clusterOperator.getBucketOperator(TEST_BUCKET);
    protected String bucketName = TEST_BUCKET;
    protected String scopeName = bucketOperator.createTestScope();
    protected String collectionName = bucketOperator.createTestCollection(scopeName);
    protected String indexName = INDEX;
}
