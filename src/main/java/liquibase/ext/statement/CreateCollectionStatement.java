package liquibase.ext.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.collection.CollectionSpec;

import liquibase.ext.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CreateCollectionStatement extends CouchbaseStatement {

    private final String collectionName;

    @Override
    public void execute(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        //todo will provide full info
        Bucket travels = cluster.bucket("test");
        travels.collections().createCollection(CollectionSpec.create(collectionName));
    }
}
