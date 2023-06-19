package common.operators;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.collection.CollectionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class TestBucketOperatorTest {

    @Mock
    private Bucket bucket;
    @Mock
    private Scope scope;
    @Mock
    private CollectionManager collectionManager;
    @InjectMocks
    private TestBucketOperator bucketOperator;


    @BeforeEach
    void setUp() {
        when(bucket.scope(anyString())).thenReturn(scope);
        when(bucket.collections()).thenReturn(collectionManager);
        doNothing().when(collectionManager).createScope(anyString());
        doNothing().when(collectionManager).createCollection(any());
        doNothing().when(bucket).waitUntilReady(any());
    }

    @Test
    void Should_return_collection_operator() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);

        assertNotNull(collectionOperator);
        verify(bucket).scope(TEST_SCOPE);
        verify(scope).collection(TEST_COLLECTION);
    }

    @Test
    void Should_create_test_scope() {
        String result = bucketOperator.createTestScope();

        verify(bucket).waitUntilReady(any());
        assertThat(result).startsWith(TEST_SCOPE);
        assertThat(result.replace(TEST_SCOPE + "_", "")).isAlphanumeric();
    }

    @Test
    void Should_create_test_collection() {
        String result = bucketOperator.createTestCollection(TEST_SCOPE);

        verify(bucket).waitUntilReady(any());
        verify(collectionManager).createCollection(any());
        assertThat(result).startsWith(TEST_COLLECTION);
        assertThat(result.replace(TEST_COLLECTION + "_", "")).isAlphanumeric();
    }
}