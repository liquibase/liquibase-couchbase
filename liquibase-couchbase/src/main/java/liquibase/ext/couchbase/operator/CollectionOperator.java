package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Common facade on {@link Bucket} including all common operations <br > and state checks
 */
@RequiredArgsConstructor
public class CollectionOperator {

    @Getter
    protected final Collection collection;

    public void insertDoc(Document document) {
        collection.insert(document.getId(), document.getValue().mapDataToType());
    }

    public void insertDocs(Document... docs) {
        Arrays.stream(docs).forEach(this::insertDoc);
    }

    public boolean docExists(String id) {
        return collection.exists(id).exists();
    }

    public void removeDoc(Document doc) {
        collection.remove(doc.getId());
    }

    public void removeDocs(Document... docs) {
        Arrays.stream(docs).forEach(this::removeDoc);
    }

    private void upsertDocInTransaction(TransactionAttemptContext transaction,
                                        String key,
                                        Object content) {
        try {
            TransactionGetResult document = transaction.get(collection, key);
            transaction.replace(document, content);
        } catch (DocumentNotFoundException ex) {
            transaction.insert(collection, key, content);
        }
    }

    public void upsertDocsTransactionally(TransactionAttemptContext transaction, Map<String, Object> docs) {
        docs.forEach((key, jsonObject) -> upsertDocInTransaction(transaction, key, jsonObject));
    }

    public Flux<TransactionGetResult> upsertDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                        Map<String, Object> docs) {
        return Flux.fromIterable(docs.entrySet())
                .flatMap(entry -> upsertDocInTransactionReactive(transaction, entry.getKey(), entry.getValue()));
    }

    public Mono<TransactionGetResult> upsertDocInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                                     String key,
                                                                     Object object) {
        Mono<TransactionGetResult> document = transaction.get(collection.reactive(), key);
        return document.doOnNext(transactionGetResult -> transaction.replace(transactionGetResult, object))
                .onErrorResume(DocumentNotFoundException.class::isInstance,
                        throwable -> transaction.insert(collection.reactive(), key, object));
    }

    public void insertDocsTransactionally(TransactionAttemptContext transaction, Map<String, Object> docs) {
        docs.forEach((key, content) -> insertDocInTransaction(transaction, key, content));
    }

    public void insertDocInTransaction(TransactionAttemptContext transaction, String id, Object content) {
        transaction.insert(collection, id, content);
    }

    public Flux<TransactionGetResult> insertDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                        Map<String, Object> docs) {
        return Flux.fromIterable(docs.entrySet())
                .flatMap(entry -> insertDocInTransactionReactive(transaction, entry.getKey(), entry.getValue()));
    }

    public Mono<TransactionGetResult> insertDocInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                                     String id,
                                                                     Object object) {
        return transaction.insert(collection.reactive(), id, object);
    }

    public void removeDocsTransactionally(TransactionAttemptContext transaction, List<Id> idList) {
        idList.forEach(id -> removeDocTransactionally(transaction, id.getId()));
    }

    private void removeDocTransactionally(TransactionAttemptContext transaction, String id) {
        TransactionGetResult result = transaction.get(collection, id);
        transaction.remove(result);
    }

    public Flux<TransactionGetResult> removeDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                        List<Id> idList) {
        return Flux.fromIterable(idList)
                .flatMap(id -> removeDocTransactionallyReactive(transaction, id.getId()));
    }

    public Mono<TransactionGetResult> removeDocTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                       String id) {
        Mono<TransactionGetResult> document = transaction.get(collection.reactive(), id);
        return document.doOnNext(transaction::remove);
    }
}
