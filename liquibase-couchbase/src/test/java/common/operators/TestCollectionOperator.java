package common.operators;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;

import java.util.concurrent.atomic.AtomicLong;

public class TestCollectionOperator extends CollectionOperator {
    private static final AtomicLong id = new AtomicLong();
    private static final int MAX_FIELDS_IN_DOC = 3;

    public TestCollectionOperator(Collection collection) {
        super(collection);
    }

    public Document generateTestDoc() {
        String docId = "docId_" + id.getAndIncrement();
        JsonObject content = JsonObject.create();
        for (int i = 1; i < MAX_FIELDS_IN_DOC; i++) {
            content.put("field" + i, "value" + i);
        }
        return Document.document(docId, new String(content.toBytes()), DataType.JSON);
    }
}
