package common.operators;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.operator.CollectionOperator;

import java.util.Random;

public class TestCollectionOperator extends CollectionOperator {
    private final Random random;

    public TestCollectionOperator(Collection collection) {
        super(collection);
        random = new Random();
    }

    public String insertTestDoc(JsonObject content) {
        String id = "docId_" + random.nextInt();
        collection.insert(id, content);
        return id;
    }

}
