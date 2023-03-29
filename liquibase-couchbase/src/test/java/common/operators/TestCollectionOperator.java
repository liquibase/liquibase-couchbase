package common.operators;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;

import java.util.concurrent.atomic.AtomicLong;

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

    public static JsonObject createOneFieldJson(String id, String value) {
        return JsonObject.create().put(id, value);
    }

    public static JsonObject createTestDocContent() {
        return createOneFieldJson("key", "value");
    }
}
