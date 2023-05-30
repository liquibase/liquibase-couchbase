package common.operators;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mockito.stubbing.Answer;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class TestClusterOperatorTest {

    @Mock
    private Cluster cluster;
    @Mock
    private Bucket bucket;
    @Mock
    private BucketManager bucketManager;
    @Mock
    private BucketSettings bucketSettings;
    @Mock
    private Scope scope;
    @Mock
    private Collection collection;
    @Mock
    private CollectionQueryIndexManager collectionQueryIndexManager;

    @InjectMocks
    private TestClusterOperator testClusterOperator;

    @BeforeEach
    void setUp() {
        when(cluster.bucket(anyString())).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucketManager.getBucket(anyString())).thenReturn(bucketSettings);
        doNothing().when(bucketManager).createBucket(any(BucketSettings.class));
        when(bucket.scope(anyString())).thenReturn(scope);
        when(scope.collection(anyString())).thenReturn(collection);
        when(collection.queryIndexes()).thenReturn(collectionQueryIndexManager);
        doNothing().when(collectionQueryIndexManager).createIndex(anyString(), anyList(), any(CreateQueryIndexOptions.class));
    }

    @Test
    void should_return_bucket_operator() {
        TestBucketOperator result = testClusterOperator.getBucketOperator(TEST_BUCKET);

        assertNotNull(result);
        assertEquals(result.getBucket(), bucket);
    }

    @Test
    void should_create_bucket_for_bucket_operator() {
        when(bucketManager.getBucket(TEST_BUCKET)).thenAnswer(new Answer<BucketSettings>() {
            private int count = 0;

            public BucketSettings answer(InvocationOnMock invocation) {
                if (count++ == 0) { throw new BucketNotFoundException(EMPTY); }

                return bucketSettings;
            }
        });

        doNothing().when(bucketManager).createBucket(any(BucketSettings.class));
        TestBucketOperator result = testClusterOperator.getOrCreateBucketOperator(TEST_BUCKET);

        verify(bucketManager).createBucket(any(BucketSettings.class));
        assertEquals(result.getBucket(), bucket);
    }

    @Test
    void should_create_collection_primary_index() {
        testClusterOperator.getBucketOperator(TEST_BUCKET)
                .getCollectionOperator(TEST_COLLECTION, TEST_SCOPE)
                .createPrimaryIndex();

        verify(collectionQueryIndexManager).createPrimaryIndex();
    }


    @Test
    void should_generate_index_id() {
        String result = testClusterOperator.getTestIndexId();

        assertThat(result).isNotBlank();
        assertTrue(NumberUtils.isDigits(result.replace("testIndex_", "")));
    }
}