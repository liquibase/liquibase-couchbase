package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.deps.com.google.common.collect.ImmutableList;
import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.retry.FailFastRetryStrategy;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionQueryResult;
import liquibase.ext.couchbase.types.BucketScope;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.couchbase.client.java.manager.bucket.CreateBucketOptions.createBucketOptions;
import static com.couchbase.client.java.manager.bucket.UpdateBucketOptions.updateBucketOptions;
import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static com.couchbase.client.java.manager.query.CreateQueryIndexOptions.createQueryIndexOptions;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_CONTENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.entry;
import static org.assertj.core.api.CollectionAssert.assertThatCollection;
import static org.assertj.core.api.MapAssert.assertThatMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class ClusterOperatorTest {

    private static final String TEST_INDEX = "testIndex";
    private static final String TEST_QUERY = "SELECT 1";
    @Mock
    private Cluster cluster;
    @Mock
    private Bucket bucket;
    @Mock
    private BucketManager bucketManager;
    @Mock
    private BucketSettings bucketSettings;
    @Mock
    private QueryIndexManager queryIndexManager;
    @Mock
    private Scope scope;
    @Mock
    private Collection collection;
    @Mock
    private CollectionQueryIndexManager collectionQueryIndexManager;
    @Mock
    private QueryIndex queryIndex;
    @Mock
    private QueryResult queryResult;
    @Mock
    private TransactionQueryResult transactionQueryResult;
    @Mock
    private CollectionOperator collectionOperator;
    @Mock
    private TransactionAttemptContext transactionContext;

    @InjectMocks
    private ClusterOperator clusterOperator;

    @BeforeEach
    void setUp() {
        when(cluster.bucket(anyString())).thenReturn(bucket);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucketManager.getBucket(anyString())).thenReturn(bucketSettings);
        when(cluster.queryIndexes()).thenReturn(queryIndexManager);
        doNothing().when(queryIndexManager).createPrimaryIndex(anyString());
        doNothing().when(collectionOperator).createPrimaryIndex();
        doNothing().when(queryIndexManager).createPrimaryIndex(anyString(), any(CreatePrimaryQueryIndexOptions.class));
        when(bucket.scope(anyString())).thenReturn(scope);
        when(scope.collection(anyString())).thenReturn(collection);
        when(collection.queryIndexes()).thenReturn(collectionQueryIndexManager);
        doNothing().when(collectionQueryIndexManager).createIndex(anyString(), anyList(), any(CreateQueryIndexOptions.class));
    }

    @Test
    void Should_return_bucket_operator() {
        BucketOperator result = clusterOperator.getBucketOperator(TEST_BUCKET);

        assertNotNull(result);
        assertEquals(result.getBucket(), bucket);
    }


    @Test
    void Should_return_index_manager() {
        QueryIndexManager result = clusterOperator.getQueryIndexes();

        assertEquals(result, queryIndexManager);
    }

    @Test
    void Should_create_query_index() {
        List<Field> fields = ImmutableList.of(new Field(TEST_ID));
        Keyspace keyspace = Keyspace.keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .createQueryIndex(TEST_INDEX, fields, null);

        verify(collectionQueryIndexManager).createIndex(eq(TEST_INDEX), anyCollection());
    }

    @Test
    void Should_create_query_index_with_options() {
        List<Field> fields = ImmutableList.of(new Field(TEST_ID));
        CreateQueryIndexOptions options = createQueryIndexOptions();

        clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())
                .getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())
                .createQueryIndex(TEST_INDEX, fields, options);

        verify(collectionQueryIndexManager).createIndex(eq(TEST_INDEX), anyList(), eq(options));
    }

    @Test
    void Should_create_bucket_with_options() {
        BucketSettings settings = BucketSettings.create(TEST_BUCKET);
        CreateBucketOptions options = createBucketOptions();

        clusterOperator.createBucketWithOptionsAndSettings(settings, options);

        verify(bucketManager).createBucket(settings, options);
    }

    @Test
    void Should_get_query_indexes() {
        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(singletonList(queryIndex));
        List<QueryIndex> result = clusterOperator.getQueryIndexesForBucket(TEST_BUCKET);

        assertThatCollection(result).containsExactly(queryIndex);
    }

    @Test
    void Should_return_true_if_bucket_exist() {
        boolean result = clusterOperator.isBucketExists(TEST_BUCKET);

        assertTrue(result);
    }

    @Test
    void Should_return_false_if_bucket_not_exist() {
        when(bucketManager.getBucket(TEST_BUCKET)).thenThrow(BucketNotFoundException.class);
        boolean result = clusterOperator.isBucketExists(TEST_BUCKET);

        assertFalse(result);
    }

    @Test
    void Should_drop_collection_query_index() {
        clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())
                .getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())
                .dropIndex(TEST_INDEX);

        verify(collectionQueryIndexManager).dropIndex(TEST_INDEX);
    }

    @Test
    void Should_return_true_if_index_exist() {
        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(singletonList(queryIndex));
        when(queryIndex.name()).thenReturn(TEST_INDEX);

        boolean result = clusterOperator.indexExists(TEST_INDEX, TEST_BUCKET);

        assertTrue(result);
    }

    @Test
    void Should_return_false_if_index_not_exist() {
        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(Collections.emptyList());

        boolean result = clusterOperator.indexExists(TEST_INDEX, TEST_BUCKET);

        assertFalse(result);
    }

    @Test
    void Should_return_true_if_collection_index_exist() {
        when(collectionQueryIndexManager.getAllIndexes()).thenReturn(singletonList(queryIndex));
        when(queryIndex.name()).thenReturn(TEST_INDEX);

        boolean result = clusterOperator
                .getBucketOperator(TEST_BUCKET)
                .getCollectionOperator(TEST_COLLECTION, TEST_SCOPE)
                .collectionIndexExists(TEST_INDEX);

        assertTrue(result);
    }

    @Test
    void Should_return_false_if_collection_index_not_exist() {
        when(collectionQueryIndexManager.getAllIndexes()).thenReturn(Collections.emptyList());

        boolean result = clusterOperator.getBucketOperator(TEST_BUCKET)
                .getCollectionOperator(TEST_COLLECTION, TEST_SCOPE)
                .collectionIndexExists(TEST_INDEX);

        assertFalse(result);
    }

    @Test
    void Should_drop_primary_index() {
        when(collectionQueryIndexManager.getAllIndexes()).thenReturn(Collections.emptyList());

        clusterOperator.getBucketOperator(TEST_BUCKET)
                .getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())
                .dropCollectionPrimaryIndex();

        verify(collectionQueryIndexManager).dropPrimaryIndex();
    }

    @Test
    void Should_transform_docs() {
        Document doc = Document.document(TEST_ID, TEST_CONTENT);
        List<Document> documents = ImmutableList.of(doc);

        Map<String, Object> result = clusterOperator.checkDocsAndTransformToObjects(documents);

        assertThatMap(result).containsExactly(entry(TEST_ID, TEST_CONTENT));
    }

    @Test
    void Should_throw_exception_when_error_parsing() {
        Document document = mock(Document.class);
        List<Document> documents = ImmutableList.of(document);

        when(document.getValue()).thenThrow(new IllegalArgumentException());

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> clusterOperator.checkDocsAndTransformToObjects(documents))
                .withMessage("Error parsing the document from the list provided");
    }


    @Test
    void Should_create_bucket() {
        doNothing().when(bucketManager).createBucket(any(BucketSettings.class));
        clusterOperator.createBucket(TEST_BUCKET);

        verify(bucketManager).createBucket(any(BucketSettings.class));
    }

    @Test
    void Should_update_bucket() {
        BucketSettings settings = BucketSettings.create(TEST_BUCKET);
        UpdateBucketOptions options = updateBucketOptions().retryStrategy(FailFastRetryStrategy.INSTANCE);

        clusterOperator.updateBucketWithOptionsAndSettings(settings, options);

        verify(bucketManager).updateBucket(settings, options);
    }

    @Test
    void Should_drop_bucket() {
        clusterOperator.dropBucket(TEST_BUCKET);

        verify(bucketManager).dropBucket(TEST_BUCKET);
    }

    @Test
    void Should_execute_sql() {
        String query = "SELECT 1";
        when(cluster.query(query)).thenReturn(queryResult);
        List<String> queries = ImmutableList.of(query);

        List<QueryResult> result = clusterOperator.executeSql(queries);

        verify(cluster).query(query);
        assertThatCollection(result).containsExactly(queryResult);
    }

    @Test
    void Should_execute_sql_in_transaction() {
        List<String> queries = ImmutableList.of(TEST_QUERY);
        when(transactionContext.query(TEST_QUERY)).thenReturn(transactionQueryResult);

        List<TransactionQueryResult> result = clusterOperator.executeSql(transactionContext, queries);

        verify(transactionContext).query(TEST_QUERY);
        assertThatCollection(result).containsExactly(transactionQueryResult);
    }

    @Test
    void Sshould_create_collection_primary_index_with_options() {
        CreatePrimaryQueryIndexOptions options = createPrimaryQueryIndexOptions();

        clusterOperator.getBucketOperator(TEST_KEYSPACE.getBucket())
                .getCollectionOperator(TEST_KEYSPACE.getCollection(), TEST_KEYSPACE.getScope())
                .createCollectionPrimaryIndex(options);

        verify(collectionQueryIndexManager).createPrimaryIndex(options);
    }

    @Test
    void Should_retrieve_document_ids_by_where_clause() {
        String getIdByWhereTemplate = "SELECT meta().id FROM %s WHERE %s";
        String whereClause = "name = 'Joe'";
        JsonObject returnedObject1 = JsonObject.create().put("id", "id1").put("name", "Joe").put("type", "customer");
        JsonObject returnedObject2 = JsonObject.create().put("id", "id2").put("name", "Joe").put("type", "employee");
        List<String> expectedIds = Arrays.asList("id1", "id2");

        when(cluster.query(format(getIdByWhereTemplate, TEST_KEYSPACE.getFullPath(), whereClause))).thenReturn(queryResult);
        when(queryResult.rowsAsObject()).thenReturn(Arrays.asList(returnedObject1, returnedObject2));

        List<String> returnedIds = clusterOperator.retrieveDocumentIdsByWhereClause(TEST_KEYSPACE, whereClause);
        assertEquals(expectedIds, returnedIds);
    }

    @Test
    void Should_return_that_index_exists() {
        String indexName = "someIndex";
        BucketScope bucketScope = BucketScope.bucketScope(TEST_BUCKET, TEST_SCOPE);
        List<QueryIndex> queryIndices = singletonList(queryIndex);

        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(queryIndices);
        when(queryIndex.scopeName()).thenReturn(Optional.of(TEST_SCOPE));
        when(queryIndex.name()).thenReturn(indexName);

        assertTrue(clusterOperator.indexExists(indexName, bucketScope));
    }

    @Test
    void Should_return_that_index_not_exists() {
        String indexName = "someIndex";
        BucketScope bucketScope = BucketScope.bucketScope(TEST_BUCKET, TEST_SCOPE);
        List<QueryIndex> queryIndices = singletonList(queryIndex);

        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(queryIndices);
        when(queryIndex.scopeName()).thenReturn(Optional.of(TEST_SCOPE));
        when(queryIndex.name()).thenReturn("anotherIndex");

        assertFalse(clusterOperator.indexExists(indexName, bucketScope));
    }

    @Test
    void Should_return_that_primary_index_exists() {
        String indexName = "somePrimaryIndex";
        List<QueryIndex> queryIndices = singletonList(queryIndex);

        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(queryIndices);
        when(queryIndex.primary()).thenReturn(true);
        when(queryIndex.name()).thenReturn(indexName);

        assertTrue(clusterOperator.primaryIndexExists(indexName, TEST_BUCKET));
    }

    @Test
    void Should_return_that_primary_not_index_exists() {
        String indexName = "somePrimaryIndex";
        List<QueryIndex> queryIndices = singletonList(queryIndex);

        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(queryIndices);
        when(queryIndex.primary()).thenReturn(true);
        when(queryIndex.name()).thenReturn("anotherPrimaryIndex");

        assertFalse(clusterOperator.primaryIndexExists(indexName, TEST_BUCKET));
    }

    @Test
    void Should_return_that_primary_index_exists_when_bucketScope_provided() {
        String indexName = "somePrimaryIndex";
        BucketScope bucketScope = BucketScope.bucketScope(TEST_BUCKET, TEST_SCOPE);
        List<QueryIndex> queryIndices = singletonList(queryIndex);

        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(queryIndices);
        when(queryIndex.primary()).thenReturn(true);
        when(queryIndex.scopeName()).thenReturn(Optional.of(TEST_SCOPE));
        when(queryIndex.name()).thenReturn(indexName);

        assertTrue(clusterOperator.primaryIndexExists(indexName, bucketScope));
    }

    @Test
    void Should_return_that_primary_index_not_exists_when_bucketScope_provided() {
        String indexName = "somePrimaryIndex";
        BucketScope bucketScope = BucketScope.bucketScope(TEST_BUCKET, TEST_SCOPE);
        List<QueryIndex> queryIndices = singletonList(queryIndex);

        when(queryIndexManager.getAllIndexes(TEST_BUCKET)).thenReturn(queryIndices);
        when(queryIndex.primary()).thenReturn(true);
        when(queryIndex.scopeName()).thenReturn(Optional.of(TEST_SCOPE));
        when(queryIndex.name()).thenReturn("anotherPrimaryIndexName");

        assertFalse(clusterOperator.primaryIndexExists(indexName, bucketScope));
    }

    @Test
    void Should_execute_single_sql() {
        String statement = "Select * from testBucket.testScope.testCollection";

        when(cluster.query(statement)).thenReturn(queryResult);

        QueryResult returnedResult = clusterOperator.executeSingleSql(statement);
        assertNotNull(returnedResult);
        verify(cluster).query(statement);
    }

}