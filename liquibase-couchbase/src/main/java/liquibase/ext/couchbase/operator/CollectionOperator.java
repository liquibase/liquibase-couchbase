package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.types.Document;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;

/**
 * Common facade on {@link Bucket} including all common operations <br >
 * and state checks
 */
@RequiredArgsConstructor
public class CollectionOperator {

    @Getter
    protected final Collection collection;

    public void insertDoc(String id, JsonObject content) {
        collection.insert(id, content);
    }

    public void insertDocInTransaction(TransactionAttemptContext transaction, String id, JsonObject content) {
        transaction.insert(collection, id, content);
    }

    public void insertDoc(Document document) {
        collection.insert(document.getId(), document.getContentAsObject());
    }

    public boolean docExists(String id) {
        return collection.exists(id).exists();
    }

    public void removeDoc(String id) {
        collection.remove(id);
    }

    public void removeDocs(String... ids) {
        Arrays.stream(ids).forEach(collection::remove);
    }

    public void upsertDoc(String id, JsonObject content) {
        collection.upsert(id, content);
    }

    private void upsertDocInTransaction(TransactionAttemptContext transaction,
                        String key,
                        JsonObject jsonObject) {
        try {
            TransactionGetResult document = transaction.get(collection, key);
            transaction.replace(document, jsonObject);
        } catch (DocumentNotFoundException ex) {
            transaction.insert(collection, key, jsonObject);
        }
    }

    public void upsertDocs(Map<String, JsonObject> docs) {
        docs.forEach(this::upsertDoc);
    }

    public void upsertDocsTransactionally(TransactionAttemptContext transaction, Map<String, JsonObject> docs) {
        docs.forEach((key, jsonObject) -> upsertDocInTransaction(transaction, key, jsonObject));
    }

    public void insertDocs(Map<String, JsonObject> docs) {
        docs.forEach(this::insertDoc);
    }

    public void insertDocsTransactionally(TransactionAttemptContext transaction, Map<String, JsonObject> docs) {
        docs.forEach((key, jsonObject) -> insertDocInTransaction(transaction, key, jsonObject));
    }
}
