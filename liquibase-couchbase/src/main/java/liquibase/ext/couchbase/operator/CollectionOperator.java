package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionGetResult;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public void insertDoc(String id, JsonObject content) {
        collection.insert(id, content);
    }

    public void insertDocInTransaction(TransactionAttemptContext transaction, String id, Object content) {
        transaction.insert(collection, id, content);
    }

    public void insertDoc(Document document) {
        collection.insert(document.getId(), document.getValue().mapDataToType());
    }

    public boolean docExists(String id) {
        return collection.exists(id).exists();
    }

    public void removeDoc(String id) {
        collection.remove(id);
    }

    public void removeDoc(Document doc) {
        removeDoc(doc.getId());
    }

    public void removeDocs(String... ids) {
        Arrays.stream(ids).forEach(collection::remove);
    }

    public void upsertDoc(String id, JsonObject content) {
        collection.upsert(id, content);
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
        docs.forEach((key, content) -> upsertDocInTransaction(transaction, key, content));
    }

    public void insertDocsTransactionally(TransactionAttemptContext transaction, Map<String, Object> docs) {
        docs.forEach((key, content) -> insertDocInTransaction(transaction, key, content));
    }

    public void removeDocsTransactionally(TransactionAttemptContext transaction,  List<Id> idList) {
        idList.forEach(id -> removeDocTransactionally(transaction, id.getId()));
    }

    private void removeDocTransactionally(TransactionAttemptContext transaction, String id) {
        TransactionGetResult result = transaction.get(collection, id);
        transaction.remove(result);
    }
}
