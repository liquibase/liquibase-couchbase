package liquibase.ext.couchbase.statement;


import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InsertManyStatement extends CouchbaseStatement {

    private final String bucketName;
    private final Map<String, String> documents;
    private final String scopeName;
    private final String collectionName;

    @Override
    public void execute(CouchbaseConnection connection) {
        Map<String, JsonObject> contentList = checkDocsAndTransformToJsons();
        Cluster cluster = connection.getCluster();
        Bucket bucket = cluster.bucket(bucketName);
        final Collection collection;
        if (isNotBlank(scopeName)) {
            Scope scope = bucket.scope(scopeName);
            collection = scope.collection(collectionName);

        } else {
            collection = isBlank(collectionName) ?
                    bucket.defaultCollection() : bucket.collection(collectionName);
        }
        contentList.forEach(collection::insert);
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

