package liquibase.ext.couchbase.statement;


import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.wdt.couchbase.Keyspace;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertManyStatement extends CouchbaseStatement {
    private final Keyspace keyspace;
    private final List<Document> documents;

    @Override
    public void execute(CouchbaseConnection connection) {
        Map<String, JsonObject> contentList = checkDocsAndTransformToJsons();
        final Collection collection = getCollection(connection);

        contentList.forEach(collection::insert);
    }

    private Collection getCollection(CouchbaseConnection connection) {
        return connection.getCluster().bucket(keyspace.getBucket())
                .scope(keyspace.getScope()).collection(keyspace.getCollection());
    }

    private Map<String, JsonObject> checkDocsAndTransformToJsons() {
        try {
            return documents.stream()
                    .collect(Collectors.toMap(Document::getId, ee -> JsonObject.fromJson(ee.getContent())));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException("Error parsing the document from the list provided", ex);
        }
    }
}

