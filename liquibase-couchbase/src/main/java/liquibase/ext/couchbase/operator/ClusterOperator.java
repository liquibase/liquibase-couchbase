package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionQueryResult;
import liquibase.ext.couchbase.types.Document;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringUtils.isBlank;

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
        return new BucketOperator(cluster.bucket(bucket));
    }

    public CollectionOperator getCollectionOperator(Collection collection) {
        return new CollectionOperator(collection);
    }

    protected void requireBucketExists(@NonNull String bucketName) throws BucketNotFoundException {
        cluster.buckets().getBucket(bucketName);
    }

    public void createBucketWithOptionsAndSettings(BucketSettings settings, CreateBucketOptions options) {
        cluster.buckets().createBucket(settings, options);
    }

    public void createBucket(String name) {
        cluster.buckets().createBucket(BucketSettings.create(name));
    }

    public boolean isBucketExists(String name) {
        try {
            cluster.buckets().getBucket(name);
            return true;
        } catch (BucketNotFoundException ex) {
            return false;
        }
    }

    public void updateBucketWithOptionsAndSettings(BucketSettings settings, UpdateBucketOptions options) {
        cluster.buckets().updateBucket(settings, options);
    }

    public void dropBucket(String bucketName) {
        cluster.buckets().dropBucket(bucketName);
    }

    public QueryIndexManager getQueryIndexes() {
        return cluster.queryIndexes();
    }

    public List<QueryIndex> getQueryIndexesForBucket(String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName);
    }

    public boolean indexExists(String indexName, String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName).stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public boolean indexExists(String indexName, String bucketName, String scopeName, boolean isPrimary) {
        return getQueryIndexes().getAllIndexes(bucketName).stream()
                .filter(queryIndex -> queryIndex.primary() == isPrimary)
                .filter(queryIndex -> isBlank(scopeName) || queryIndex.scopeName().filter(scopeName::equals).isPresent())
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public List<TransactionQueryResult> executeSql(TransactionAttemptContext transaction, List<String> queries) {
        return queries.stream().map(transaction::query).collect(toList());
    }

    public List<QueryResult> executeSql(List<String> queries) {
        return queries.stream().map(cluster::query).collect(toList());
    }

    public Map<String, Object> checkDocsAndTransformToObjects(List<Document> documents) {
        try {
            return documents.stream()
                    .collect(toMap(Document::getId, ee -> ee.getValue().mapDataToType()));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Error parsing the document from the list provided", ex);
        }
    }

}
