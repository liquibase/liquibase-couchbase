package liquibase.ext.couchbase.statement;


import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertManyStatement extends CouchbaseStatement {

    private final String bucketName;
    private final Map<String, String> documents;
    private final String scopeName;
    private final String collectionName;

    @Override
    public void execute(CouchbaseConnection connection) {
        Map<String, JsonObject> contentList = checkDocsAndTransformToJsons();
        final Collection collection = getCollection(connection);

        contentList.forEach(collection::upsert);
    }

    private Collection getCollection(CouchbaseConnection connection) {
        return connection.getCluster().bucket(bucketName)
                .scope(scopeName).collection(collectionName);
    }

    private Map<String, JsonObject> checkDocsAndTransformToJsons() {
        try {
            return documents.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, ee -> JsonObject.fromJson(ee.getValue())));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException("Error parsing the document from the list provided", ex);
        }
    }
}

