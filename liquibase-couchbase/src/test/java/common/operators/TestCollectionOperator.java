package common.operators;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static liquibase.ext.couchbase.types.Document.document;

public class TestCollectionOperator extends CollectionOperator {
    private static final AtomicLong id = new AtomicLong();
    private static final int MAX_FIELDS_IN_DOC = 3;
    private static final String TEST_DOC_ID = "docId";

    public TestCollectionOperator(Collection collection) {
        super(collection);
    }

    public Document generateTestDoc() {
        JsonObject content = JsonObject.create();
        for (int i = 1; i < MAX_FIELDS_IN_DOC; i++) {
            content.put("field" + i, "value" + i);
        }
        String docId = TEST_DOC_ID + "_" + id.getAndIncrement();
        return document(docId, content);
    }

    public Document generateTestDocById(String docId) {
        JsonObject content = JsonObject.create();
        for (int i = 1; i < MAX_FIELDS_IN_DOC; i++) {
            content.put("field" + i, "value" + i);
        }
        return document(docId, content);
    }

    public Document generateTestDocByBody(JsonObject jsonObject) {
        return document(UUID.randomUUID().toString(), jsonObject);
    }

    public static JsonObject createOneFieldJson(String id, String value) {
        return JsonObject.create().put(id, value);
    }

    public static JsonObject createTestDocContent() {
        return createOneFieldJson("key", "value");
    }

}
