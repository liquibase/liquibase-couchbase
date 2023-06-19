package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class BucketOperatorTest {

    @Mock
    private Bucket bucket;
    @Mock
    private Scope scope;
    @Mock
    private ScopeSpec scopeSpec;
    @Mock
    private Collection collection;
    @Mock
    private CollectionManager collectionManager;
    @Mock
    private CollectionSpec collectionSpec;
    @InjectMocks
    private BucketOperator bucketOperator;


    @BeforeEach
    void setUp() {
        when(bucket.scope(anyString())).thenReturn(scope);
        when(scope.collection(anyString())).thenReturn(collection);
        when(scope.name()).thenReturn(TEST_SCOPE);
        when(bucket.collections()).thenReturn(collectionManager);
        doNothing().when(collectionManager).createScope(anyString());
        doNothing().when(collectionManager).dropScope(anyString());
        doNothing().when(collectionManager).createCollection(any());
        doNothing().when(collectionManager).dropCollection(any());
        when(scopeSpec.collections()).thenReturn(Collections.singleton(collectionSpec));
        when(collectionSpec.scopeName()).thenReturn(TEST_SCOPE);
        when(collectionSpec.name()).thenReturn(TEST_COLLECTION);
        when(bucket.defaultScope()).thenReturn(scope);
    }

    @Test
    void Should_return_collection_operator() {
        CollectionOperator result = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
        assertEquals(collection, result.getCollection());
    }

    @Test
    void Should_create_scope() {
        bucketOperator.createScope(TEST_SCOPE);
        verify(collectionManager).createScope(TEST_SCOPE);
    }

    @Test
    void Should_get_scope() {
        Scope result = bucketOperator.getScope(TEST_SCOPE);

        assertEquals(scope, result);
    }

    @Test
    void Should_get_collection_if_exist() {
        Collection result = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

        assertEquals(result, collection);
    }

    @Test
    void Should_get_collection_from_default_scope() {
        Collection result = bucketOperator.getCollectionFromDefaultScope(TEST_COLLECTION);

        assertEquals(result, collection);
    }

    @Test
    void Should_drop_scope() {
        bucketOperator.dropScope(TEST_SCOPE);

        verify(collectionManager).dropScope(TEST_SCOPE);
    }

    @Test
    void Should_return_true_if_scope_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        boolean result = bucketOperator.hasScope(TEST_SCOPE);

        assertTrue(result);
    }

    @Test
    void Should_return_false_if_scope_not_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.emptyList());

        boolean result = bucketOperator.hasScope(TEST_SCOPE);

        assertFalse(result);
    }

    @Test
    void Should_return_true_if_collection_in_scope_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        boolean result = bucketOperator.hasCollectionInScope(TEST_COLLECTION, TEST_SCOPE);

        assertTrue(result);
    }

    @Test
    void Should_return_false_if_collection_in_scope_not_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        boolean result = bucketOperator.hasCollectionInScope("notExistingCollection", TEST_SCOPE);

        assertFalse(result);
    }

    @Test
    void Should_return_true_if_collection_in_default_scope_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        boolean result = bucketOperator.hasCollectionInDefaultScope(TEST_COLLECTION);

        assertTrue(result);
    }

    @Test
    void Should_return_false_if_collection_in_default_scope_not_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        boolean result = bucketOperator.hasCollectionInDefaultScope("notExistingCollection");

        assertFalse(result);
    }

    @Test
    void Should_create_collection() {
        bucketOperator.createCollection("notExistingCollection", TEST_SCOPE);

        verify(collectionManager).createCollection(any());
    }

    @Test
    void Should_create_collection_in_default_scope() {
        bucketOperator.createCollectionInDefaultScope("notExistingCollection");

        verify(collectionManager).createCollection(any());
    }

    @Test
    void Should_drop_collection() {
        bucketOperator.dropCollection(TEST_COLLECTION, TEST_SCOPE);

        verify(collectionManager).dropCollection(any());
    }

    @Test
    void Should_drop_collection_in_default_scope() {
        bucketOperator.dropCollectionInDefaultScope(TEST_COLLECTION);

        verify(collectionManager).dropCollection(any());
    }

    @Test
    void Should_get_scope_if_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        bucketOperator.getOrCreateScope(TEST_SCOPE);

        verify(bucket).scope(TEST_SCOPE);
    }

    @Test
    void Should_create_and_get_scope_if_not_exist() {
        when(collectionManager.getAllScopes()).thenReturn(Collections.emptyList());

        bucketOperator.getOrCreateScope(TEST_SCOPE);

        verify(collectionManager).createScope(TEST_SCOPE);
        verify(bucket).scope(TEST_SCOPE);
    }

}