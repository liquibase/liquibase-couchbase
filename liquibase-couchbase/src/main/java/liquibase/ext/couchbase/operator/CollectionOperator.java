package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Common facade on {@link Bucket} including all common operations <br >
 * and state checks
 */
@RequiredArgsConstructor
public class CollectionOperator {

    @Getter
    private final Collection collection;

    public void insertDoc(String id, JsonObject content) {
        collection.insert(id, content);
    }

    public void removeDoc(String id) {
        collection.remove(id);
    }

    public void removeDocs(String... ids) {
        Arrays.stream(ids).forEach(collection::remove);
    }

}
