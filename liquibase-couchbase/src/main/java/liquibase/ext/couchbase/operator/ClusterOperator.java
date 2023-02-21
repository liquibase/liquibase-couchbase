package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.core.error.InvalidArgumentException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import com.couchbase.client.java.manager.query.GetAllQueryIndexesOptions;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * A part of a facade package for Couchbase Java SDK.
 * Provides access to {@link Cluster} common operations and state checks.
 *
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

    public void createPrimaryIndex(String bucket) {
        getQueryIndexes().createPrimaryIndex(bucket);
    }

    public void createQueryIndex(String indexName, String bucket, List<Field> fields,
                                 CreateQueryIndexOptions options) {
        List<String> fieldList = fields.stream()
                .map(Field::getField)
                .collect(toList());
        cluster.queryIndexes().createIndex(bucket, indexName, fieldList, options);
    }

    public void createBucketWithOptionsAndSettings(BucketSettings settings, CreateBucketOptions options) {
        cluster.buckets().createBucket(settings, options);
    }

    public QueryIndexManager getQueryIndexes() {
        return cluster.queryIndexes();
    }

    public List<QueryIndex> getQueryIndexesForBucket(String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName);
    }

    public void createIndex(String name, Keyspace keyspace, List<String> fieldList) {
        String bucket = keyspace.getBucket();
        CreateQueryIndexOptions options = CreateQueryIndexOptions.createQueryIndexOptions()
                .scopeName(keyspace.getScope())
                .collectionName(keyspace.getCollection());
        getQueryIndexes().createIndex(bucket, name, fieldList, options);
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
        DropQueryIndexOptions options = DropQueryIndexOptions.dropQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
        getQueryIndexes().dropIndex(keyspace.getBucket(), indexName, options);
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
        GetAllQueryIndexesOptions options = GetAllQueryIndexesOptions.getAllQueryIndexesOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
        return getQueryIndexes().getAllIndexes(keyspace.getBucket(), options).stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public void dropPrimaryIndex(Keyspace keyspace) {
        DropPrimaryQueryIndexOptions options = DropPrimaryQueryIndexOptions
                .dropPrimaryQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
        getQueryIndexes().dropPrimaryIndex(keyspace.getBucket(), options);
    }

    public Map<String, JsonObject> checkDocsAndTransformToJsons(List<Document> documents) {
        try {
            return documents.stream()
                    .collect(toMap(Document::getId, ee -> JsonObject.fromJson(ee.getContent())));
        } catch (InvalidArgumentException ex) {
            throw new IllegalArgumentException("Error parsing the document from the list provided", ex);
        }
    }

}
