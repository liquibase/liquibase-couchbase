package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * A part of a facade package for Couchbase Java SDK. Provides access to {@link Cluster} common operations and state checks.
 * @see BucketOperator
 */

@Getter
@RequiredArgsConstructor
public class ClusterOperator {

    protected final Cluster cluster;

    public BucketOperator getBucketOperator(String bucket) {
        requireBucketExists(bucket);
        return new BucketOperator(
                cluster.bucket(bucket)
        );
    }

    protected void requireBucketExists(@NonNull String bucketName) throws BucketNotFoundException {
        cluster.buckets().getBucket(bucketName);
    }

    public void createPrimaryIndex(String bucket, CreatePrimaryQueryIndexOptions options) {
        getQueryIndexes().createPrimaryIndex(bucket, options);
    }

    public void createCollectionPrimaryIndex(Keyspace keyspace, CreatePrimaryQueryIndexOptions options) {
        Collection col = cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        if (options != null) {
            col.queryIndexes().createPrimaryIndex(options);
        }
        else {
            col.queryIndexes().createPrimaryIndex();
        }
    }

    public void createPrimaryIndex(String bucket) {
        getQueryIndexes().createPrimaryIndex(bucket);
    }

    public void createPrimaryIndex(Keyspace keyspace) {
        CreatePrimaryQueryIndexOptions options = createPrimaryQueryIndexOptions()
                .scopeName(keyspace.getScope())
                .collectionName(keyspace.getCollection());
        getQueryIndexes().createPrimaryIndex(keyspace.getBucket(), options);
    }

    public void createQueryIndex(String indexName, Keyspace keyspace, List<Field> fields,
                                 CreateQueryIndexOptions options) {
        List<String> fieldList = fields.stream()
                .map(Field::getField)
                .collect(toList());
        Collection collection = cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        collection.queryIndexes().createIndex(indexName, fieldList, options);
    }

    public void createBucketWithOptionsAndSettings(BucketSettings settings, CreateBucketOptions options) {
        cluster.buckets().createBucket(settings, options);
    }

    public void createBucket(String name) {
        cluster.buckets().createBucket(BucketSettings.create(name));
    }

    public void updateBucketWithOptionsAndSettings(BucketSettings settings, UpdateBucketOptions options) {
        cluster.buckets().updateBucket(settings, options);
    }

    public void dropBucket(String bucketName) {
        cluster.buckets().dropBucket(bucketName);
    }

    public void executeN1ql(TransactionAttemptContext transaction, List<String> queries) {
        queries.forEach(transaction::query);
    }

    public void executeN1ql(List<String> queries) {
        queries.forEach(cluster::query);
    }

    public QueryIndexManager getQueryIndexes() {
        return cluster.queryIndexes();
    }

    public List<QueryIndex> getQueryIndexesForBucket(String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName);
    }

    public void createIndex(String name, Keyspace keyspace, List<String> fieldList) {
        Collection collection = cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        collection.queryIndexes().createIndex(name, fieldList);
    }

    public void createIndex(String name, String bucket, List<String> fieldList) {
        getQueryIndexes().createIndex(bucket, name, fieldList);
    }

    public boolean isBucketExists(String name) {
        try {
            cluster.buckets().getBucket(name);
            return true;
        } catch (BucketNotFoundException ex) {
            return false;
        }
    }

    public void dropIndex(String indexName, Keyspace keyspace) {
        Collection collection = cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        collection.queryIndexes().dropIndex(indexName);
    }

    public void dropIndex(String indexName, String bucketName) {
        getQueryIndexes().dropIndex(bucketName, indexName);
    }

    public boolean indexExists(String indexName, String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName).stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public boolean indexExists(String indexName, Keyspace keyspace) {
        Collection collection = cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        return collection.queryIndexes().getAllIndexes().stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public void dropPrimaryIndex(Keyspace keyspace) {
        Collection collection = cluster.bucket(keyspace.getBucket())
                .scope(keyspace.getScope())
                .collection(keyspace.getCollection());
        collection.queryIndexes().dropPrimaryIndex();
    }

    public void dropPrimaryIndex(String bucket, DropPrimaryQueryIndexOptions options) {
        cluster.queryIndexes().dropPrimaryIndex(bucket, options);
    }

    public Map<String, Object> checkDocsAndTransformToObjects(List<Document> documents) {
        try {
            return documents.stream()
                    .collect(toMap(Document::getId, ee -> ee.getValue().mapDataToType()));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException("Error parsing the document from the list provided", ex);
        }
    }

}
