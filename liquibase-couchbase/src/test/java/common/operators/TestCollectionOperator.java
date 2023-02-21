package common.operators;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.types.Document;

import java.util.Random;

public class TestCollectionOperator extends CollectionOperator {
    private static final int MAX_FIELDS_IN_DOC = 3;
    private final Random random;

    public TestCollectionOperator(Collection collection) {
        super(collection);
        random = new Random();
    }

    public Document generateTestDoc() {
        String id = "docId_" + random.nextInt();
        JsonObject content = JsonObject.create();
        for (int i = 1; i < MAX_FIELDS_IN_DOC; i++) {
            content.put("field" + i, "value" + i);
        }
        return Document.document(id, content);
    }
}
