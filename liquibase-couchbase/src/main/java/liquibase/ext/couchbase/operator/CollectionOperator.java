package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.transactions.ReactiveTransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;


/**
 * Common facade on {@link Bucket} including all common operations <br > and state checks
 */
@RequiredArgsConstructor
public class CollectionOperator {

    @Getter
    protected final Collection collection;

    public void createPrimaryIndex(CreatePrimaryQueryIndexOptions options) {
        queryIndexManager().createPrimaryIndex(options);
    }

    public void createCollectionPrimaryIndex(CreatePrimaryQueryIndexOptions options) {
        if (options != null) {
            queryIndexManager().createPrimaryIndex(options);
            return;
        }
        queryIndexManager().createPrimaryIndex();
    }

    public void createPrimaryIndex() {
        queryIndexManager().createPrimaryIndex();
    }

    public void createQueryIndex(String indexName, List<Field> fields,
                                 CreateQueryIndexOptions options) {
        List<String> fieldList = fields.stream()
                .map(Field::getField)
                .collect(toList());
        if (isNull(options)) {
            queryIndexManager().createIndex(indexName, fieldList);
            return;
        }
        queryIndexManager().createIndex(indexName, fieldList, options);
    }

    public void dropPrimaryIndex(DropPrimaryQueryIndexOptions options) {
        queryIndexManager().dropPrimaryIndex(options);
    }

    public void dropIndex(String indexName) {
        queryIndexManager().dropIndex(indexName);
    }

    public void dropCollectionPrimaryIndex() {
        queryIndexManager().dropPrimaryIndex();
    }

    public boolean collectionIndexExists(String indexName) {
        return queryIndexManager().getAllIndexes().stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public boolean collectionPrimaryIndexExists(String indexName) {
        return queryIndexManager()
                .getAllIndexes().stream()
                .filter(QueryIndex::primary)
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

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
                                        Document doc) {
        try {
            TransactionGetResult document = transaction.get(collection, doc.getId());
            transaction.replace(document, doc.getContentAsObject());
        } catch (DocumentNotFoundException ex) {
            transaction.insert(collection, doc.getId(), doc.getContentAsObject());
        }
    }

    public void upsertDocsTransactionally(TransactionAttemptContext transaction, List<Document> docs) {
        docs.forEach(doc -> upsertDocInTransaction(transaction, doc));
    }

    public Flux<TransactionGetResult> upsertDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                        List<Document> docs) {
        return Flux.fromIterable(docs)
                .flatMap(doc -> upsertDocInTransactionReactive(transaction, doc));
    }

    public Mono<TransactionGetResult> upsertDocInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                                     Document doc) {
        Mono<TransactionGetResult> document = transaction.get(collection.reactive(), doc.getId());
        return document.doOnNext(transactionGetResult -> transaction.replace(transactionGetResult, doc.getContentAsObject()))
                .onErrorResume(DocumentNotFoundException.class::isInstance,
                        throwable -> transaction.insert(collection.reactive(), doc.getId(), doc.getContentAsObject()));
    }

    public void insertDocsTransactionally(TransactionAttemptContext transaction, List<Document> docs) {
        docs.forEach(doc -> insertDocInTransaction(transaction, doc));
    }

    public void insertDocInTransaction(TransactionAttemptContext transaction, String id, Object content) {
        transaction.insert(collection, id, content);
    }

    public void insertDocInTransaction(TransactionAttemptContext transaction, Document document) {
        transaction.insert(collection, document.getId(), document.getContentAsObject());
    }

    public Flux<TransactionGetResult> insertDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                        List<Document> docs) {
        return Flux.fromIterable(docs).flatMap(doc -> insertDocInTransactionReactive(transaction, doc));
    }

    public Mono<TransactionGetResult> insertDocInTransactionReactive(ReactiveTransactionAttemptContext transaction,
                                                                     Document document) {
        return transaction.insert(collection.reactive(), document.getId(), document.getContentAsObject());
    }

    public void removeDocsTransactionally(TransactionAttemptContext transaction, Set<Id> idList) {
        idList.forEach(id -> removeDocTransactionally(transaction, id.getId()));
    }

    private void removeDocTransactionally(TransactionAttemptContext transaction, String id) {
        TransactionGetResult result = transaction.get(collection, id);
        transaction.remove(result);
    }

    public Flux<TransactionGetResult> removeDocsTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                        Set<Id> idList) {
        return Flux.fromIterable(idList)
                .flatMap(id -> removeDocTransactionallyReactive(transaction, id.getId()));
    }

    public Mono<TransactionGetResult> removeDocTransactionallyReactive(ReactiveTransactionAttemptContext transaction,
                                                                       String id) {
        Mono<TransactionGetResult> document = transaction.get(collection.reactive(), id);
        return document.doOnNext(transaction::remove);
    }

    private CollectionQueryIndexManager queryIndexManager() {
        return collection.queryIndexes();
    }
}
