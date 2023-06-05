package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.ReactiveCollection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.kv.MutationResult;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions.dropPrimaryQueryIndexOptions;
import static common.constants.TestConstants.MANUALLY_CREATED_INDEX;
import static common.constants.TestConstants.TEST_CONTENT;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static liquibase.ext.couchbase.types.Document.document;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class CollectionOperatorTest {

    private static final String TEST_ID_2 = "TEST_ID_2";
    private static final JsonObject TEST_CONTENT_2 = JsonObject.create().put("name", "user2").put("type", "customer2");
    private static final Document TEST_DOCUMENT_2 = document(TEST_ID_2, TEST_CONTENT_2);

    private final List<Document> documents = ImmutableList.of(TEST_DOCUMENT, TEST_DOCUMENT_2);

    @Mock
    private Collection collection;
    @Mock
    private TransactionAttemptContext transaction;
    @Mock
    private TransactionGetResult getResult;
    @Mock
    private MutationResult mutationResult;
    @Mock
    private Mono<TransactionGetResult> monoReactiveResult;
    @Mock
    private TransactionGetResult reactiveResult;
    @Mock
    private ReactiveTransactionAttemptContext reactiveTransaction;
    @Mock
    private ReactiveCollection reactiveCollection;

    @Mock
    private CollectionQueryIndexManager collectionQueryIndexManager;

    @InjectMocks
    private CollectionOperator collectionOperator;

    @BeforeEach
    void setUp() {
        when(collection.insert(anyString(), any())).thenReturn(mutationResult);
        when(collection.queryIndexes()).thenReturn(collectionQueryIndexManager);
    }

    @Test
    void should_insert_document_object() {
        Document document = document(TEST_ID, TEST_CONTENT);

        collectionOperator.insertDoc(document);

        verify(collection).insert(TEST_ID, TEST_CONTENT);
    }

    @Test
    void should_return_true_if_document_exist() {
        ExistsResult existResult = mock(ExistsResult.class);
        when(collection.exists(TEST_ID)).thenReturn(existResult);
        when(existResult.exists()).thenReturn(true);

        boolean result = collectionOperator.docExists(TEST_ID);

        assertTrue(result);
    }

    @Test
    void should_return_false_if_document_not_exist() {
        ExistsResult existResult = mock(ExistsResult.class);
        when(collection.exists(TEST_ID)).thenReturn(existResult);
        when(existResult.exists()).thenReturn(false);

        boolean result = collectionOperator.docExists(TEST_ID);

        assertFalse(result);
    }

    @Test
    void should_remove_document_by_id() {
        when(collection.remove(TEST_ID)).thenReturn(mutationResult);

        collectionOperator.removeDoc(TEST_DOCUMENT);

        verify(collection).remove(TEST_ID);
    }

    @Test
    void should_remove_document() {
        Document document = document(TEST_ID, TEST_CONTENT);

        collectionOperator.removeDoc(document);

        verify(collection).remove(TEST_ID);
    }

    @Test
    void should_remove_documents_by_id() {
        when(collection.remove(TEST_ID)).thenReturn(mutationResult);
        when(collection.remove(TEST_ID_2)).thenReturn(mutationResult);

        collectionOperator.removeDocs(TEST_DOCUMENT, TEST_DOCUMENT_2);

        verify(collection).remove(TEST_ID);
        verify(collection).remove(TEST_ID_2);
    }

//     @Test
//     void should_upsert_document() {
//         when(collection.upsert(TEST_ID, TEST_DOCUMENT)).thenReturn(mutationResult);
//
//         collectionOperator.upsertDoc(TEST_ID, TEST_DOCUMENT);
//
//         verify(collection).upsert(TEST_ID, TEST_DOCUMENT);
//     }

    @Test
    void should_insert_document_in_transaction() {
        when(transaction.insert(collection, TEST_ID, TEST_CONTENT)).thenReturn(getResult);
        ;

        collectionOperator.insertDocInTransaction(transaction, TEST_ID, TEST_CONTENT);

        verify(transaction).insert(collection, TEST_ID, TEST_CONTENT);
    }

    @Test
    void should_insert_documents_in_transaction() {
        when(transaction.insert(collection, TEST_ID, TEST_CONTENT)).thenReturn(getResult);
        when(transaction.insert(collection, TEST_ID_2, TEST_CONTENT_2)).thenReturn(getResult);

        collectionOperator.insertDocsTransactionally(transaction, documents);

        verify(transaction).insert(collection, TEST_ID, TEST_CONTENT);
        verify(transaction).insert(collection, TEST_ID_2, TEST_CONTENT_2);
    }

    @Test
    void should_upsert_document_in_transaction() {
        when(transaction.get(eq(collection), anyString())).thenReturn(getResult);
        when(transaction.replace(eq(getResult), any(JsonObject.class))).thenReturn(getResult);

        collectionOperator.upsertDocsTransactionally(transaction, documents);

        verify(transaction).get(collection, TEST_ID);
        verify(transaction).get(collection, TEST_ID_2);
        verify(transaction).replace(getResult, TEST_CONTENT);
        verify(transaction).replace(getResult, TEST_CONTENT_2);
    }

    @Test
    void should_upsert_document_if_not_exist() {
        when(transaction.get(eq(collection), anyString())).thenThrow(DocumentNotFoundException.class);
        when(transaction.insert(eq(collection), anyString(), any(JsonObject.class))).thenReturn(getResult);

        collectionOperator.upsertDocsTransactionally(transaction, documents);

        verify(transaction).get(collection, TEST_ID);
        verify(transaction).get(collection, TEST_ID_2);
        verify(transaction).insert(collection, TEST_ID, TEST_CONTENT);
        verify(transaction).insert(collection, TEST_ID_2, TEST_CONTENT_2);
    }

    @Test
    void should_remove_documents_in_transaction() {
        when(transaction.get(collection, TEST_ID)).thenReturn(getResult);
        doNothing().when(transaction).remove(getResult);
        Id testId = new Id(TEST_ID);

        collectionOperator.removeDocsTransactionally(transaction, Sets.newHashSet(testId));

        verify(transaction).get(collection, TEST_ID);
        verify(transaction).remove(getResult);
    }

    @Test
    void should_insert_document_in_reactive_transaction() {
        when(collection.reactive()).thenReturn(reactiveCollection);
        when(reactiveTransaction.insert(reactiveCollection, TEST_ID, TEST_CONTENT)).thenReturn(monoReactiveResult);

        Mono<TransactionGetResult> result =
                collectionOperator.insertDocInTransactionReactive(reactiveTransaction, TEST_DOCUMENT);
        result.subscribe();

        assertEquals(result, monoReactiveResult);
    }

    @Test
    void should_remove_document_in_reactive_transaction() {
        when(collection.reactive()).thenReturn(reactiveCollection);
        when(reactiveTransaction.get(reactiveCollection, TEST_ID)).thenReturn(Mono.just(reactiveResult));
        when(reactiveTransaction.remove(reactiveResult)).thenReturn(Mono.empty());

        Mono<TransactionGetResult> result =
                collectionOperator.removeDocTransactionallyReactive(reactiveTransaction, TEST_ID);
        result.subscribe();

        verify(reactiveTransaction).get(reactiveCollection, TEST_ID);
        verify(reactiveTransaction).remove(reactiveResult);
    }

    @Test
    void should_create_primary_index() {
        collectionOperator.createPrimaryIndex();

        verify(collectionQueryIndexManager).createPrimaryIndex();
    }

    @Test
    void should_create_primary_index_with_options() {
        CreatePrimaryQueryIndexOptions options = createPrimaryQueryIndexOptions()
                .indexName(MANUALLY_CREATED_INDEX);

        collectionOperator.createPrimaryIndex(options);

        verify(collectionQueryIndexManager).createPrimaryIndex(options);
    }

    @Test
    void should_drop_bucket_query_index() {
        String testIndex = "testIndex";
        collectionOperator.dropIndex(testIndex);

        verify(collectionQueryIndexManager).dropIndex(testIndex);
    }

    @Test
    void should_drop_primary_index_with_options() {
        DropPrimaryQueryIndexOptions options = dropPrimaryQueryIndexOptions().ignoreIfNotExists(true);

        collectionOperator.dropPrimaryIndex(options);

        verify(collectionQueryIndexManager).dropPrimaryIndex(options);
    }
}