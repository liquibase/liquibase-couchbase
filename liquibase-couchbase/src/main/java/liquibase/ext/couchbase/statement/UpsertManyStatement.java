package liquibase.ext.couchbase.statement;


import com.couchbase.client.java.json.JsonObject;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 *
 * A statement to upsert many instances of a {@link Document} into a keyspace
 *
 * @see Document
 * @see CouchbaseStatement
 * @see Keyspace
 *
 */

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpsertManyStatement extends CouchbaseStatement {
    private final Keyspace keyspace;
    private final List<Document> documents;

    @Override
    public void execute(CouchbaseConnection connection) {
        ClusterOperator clusterOperator = new ClusterOperator(connection.getCluster());
        Map<String, JsonObject> contentList = clusterOperator.checkDocsAndTransformToJsons(documents);

        clusterOperator.getBucketOperator(keyspace.getBucket())
                .getCollectionOperator(keyspace.getCollection(), keyspace.getScope())
                .upsertDocs(contentList);
    }
}

