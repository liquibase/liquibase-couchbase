package liquibase.ext.couchbase.statement;


import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.types.Keyspace;

import java.util.List;
import java.util.Map;

import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.types.Document;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import static java.util.stream.Collectors.toMap;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertManyStatement extends CouchbaseStatement {
    private final Keyspace keyspace;
    private final List<Document> documents;

    @Override
    public void execute(CouchbaseConnection connection) {
        Map<String, JsonObject> contentList = checkDocsAndTransformToJsons();
        final Collection collection = getCollection(connection);

        contentList.forEach(collection::upsert);
    }

    private Collection getCollection(CouchbaseConnection connection) {
        return connection.getCluster()
                .bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
    }

    private Map<String, JsonObject> checkDocsAndTransformToJsons() {
        try {
            return documents.stream()
                    .collect(toMap(Document::getId, ee -> JsonObject.fromJson(ee.getContent())));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException("Error parsing the document from the list provided", ex);
        }
    }
}

