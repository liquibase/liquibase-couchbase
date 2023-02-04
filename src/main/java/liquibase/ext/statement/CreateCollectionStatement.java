package liquibase.ext.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import liquibase.ext.database.CouchbaseConnection;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import static com.couchbase.client.java.manager.collection.CollectionSpec.create;

@Data
@RequiredArgsConstructor
public class CreateCollectionStatement extends CouchbaseStatement {

    private final String bucketName;
    private final String scopeName;
    private final String collectionName;

    @Override
    public void execute(CouchbaseConnection connection) {
        Cluster cluster = connection.getCluster();
        Bucket bucket = cluster.bucket(bucketName);
        bucket.collections().createCollection(create(collectionName));
    }
}
