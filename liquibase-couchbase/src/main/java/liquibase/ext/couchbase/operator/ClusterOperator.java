package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.transactions.TransactionAttemptContext;
import com.couchbase.client.java.transactions.TransactionQueryResult;
import liquibase.ext.couchbase.types.BucketScope;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * A part of a facade package for Couchbase Java SDK. Provides access to {@link Cluster} common operations and state checks.
 * @see BucketOperator
 */

@Getter
@RequiredArgsConstructor
public class ClusterOperator {

    private static final String RETRIEVE_DOCUMENT_IDS_QUERY_TEMPLATE = "SELECT meta().id FROM %s WHERE %s";

    protected final Cluster cluster;

    public BucketOperator getBucketOperator(String bucket) {
        requireBucketExists(bucket);
        return new BucketOperator(cluster.bucket(bucket));
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

    public List<String> retrieveDocumentIdsByWhereClause(Keyspace keyspace, String whereClause) {
        String collectionPath = keyspace.getFullPath();
        String documentIdRetrieveQuery = format(RETRIEVE_DOCUMENT_IDS_QUERY_TEMPLATE, collectionPath, whereClause);
        QueryResult documentIdsResult = executeSingleSql(documentIdRetrieveQuery);
        return documentIdsResult.rowsAsObject()
                .stream()
                .map(jsonObject -> jsonObject.getString("id"))
                .collect(toList());
    }

    public Set<String> retrieveDocumentIdsBySqlPlusPlusQuery(String query) {
        QueryResult documentIdsResult = executeSingleSql(query);
        return documentIdsResult.rowsAsObject()
                .stream()
                .map(jsonObject -> jsonObject.getString("id"))
                .collect(toSet());
    }

    public boolean indexExists(String indexName, String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName).stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public boolean indexExists(String indexName, BucketScope bucketScope) {
        return getQueryIndexes().getAllIndexes(bucketScope.getBucket()).stream()
                .filter(queryIndex -> bucketScope.getScope().equals(queryIndex.scopeName().get()))
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public boolean primaryIndexExists(String indexName, String bucketName) {
        return getQueryIndexes().getAllIndexes(bucketName).stream()
                .filter(QueryIndex::primary)
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public boolean primaryIndexExists(String indexName, BucketScope bucketScope) {
        return getQueryIndexes().getAllIndexes(bucketScope.getBucket()).stream()
                .filter(QueryIndex::primary)
                .filter(queryIndex -> bucketScope.getScope().equals(queryIndex.scopeName().get()))
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
    }

    public List<TransactionQueryResult> executeSql(TransactionAttemptContext transaction, List<String> queries) {
        return queries.stream().map(transaction::query).collect(toList());
    }

    public List<QueryResult> executeSql(List<String> queries) {
        return queries.stream().map(cluster::query).collect(toList());
    }

    public QueryResult executeSingleSql(String statement) {
        return cluster.query(statement);
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
