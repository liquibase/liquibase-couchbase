package liquibase.ext.couchbase.statement;

import java.util.Collections;
import java.util.HashSet;

import com.couchbase.client.core.error.BucketNotFoundException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CollectionExistsStatementTest {

    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final Bucket bucket = mock(Bucket.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final ScopeSpec scopeSpec = mock(ScopeSpec.class);
    private final CollectionSpec collectionSpec = mock(CollectionSpec.class);

    @BeforeEach
    public void configure() {
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(bucket.collections()).thenReturn(collectionManager);
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.collections()).thenReturn(new HashSet<>(Collections.singletonList(collectionSpec)));
    }

    @Test
    void Should_return_true_if_collection_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        BucketSettings bucketSettings = mock(BucketSettings.class);
        when(bucketManager.getBucket(TEST_BUCKET)).thenReturn(bucketSettings);
        when(cluster.bucket(TEST_BUCKET)).thenReturn(bucket);
        when(collectionSpec.scopeName()).thenReturn(TEST_SCOPE);
        when(collectionSpec.name()).thenReturn(TEST_COLLECTION);

        assertThat(statement.isTrue(connection)).isTrue();
    }

    @Test
    void Should_return_false_if_bucket_not_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        when(bucketManager.getBucket(TEST_BUCKET)).thenThrow(new BucketNotFoundException(NEW_TEST_BUCKET));

        assertThat(statement.isTrue(connection)).isFalse();
    }

    @Test
    void Should_return_false_if_scope_not_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        BucketSettings bucketSettings = mock(BucketSettings.class);
        when(bucketManager.getBucket(TEST_BUCKET)).thenReturn(bucketSettings);
        when(cluster.bucket(TEST_BUCKET)).thenReturn(bucket);
        when(collectionSpec.scopeName()).thenReturn("UN_EXISTS");
        when(collectionSpec.name()).thenReturn(TEST_COLLECTION);

        assertThat(statement.isTrue(connection)).isFalse();
    }

    @Test
    void Should_return_false_if_collection_not_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        BucketSettings bucketSettings = mock(BucketSettings.class);
        when(bucketManager.getBucket(TEST_BUCKET)).thenReturn(bucketSettings);
        when(cluster.bucket(TEST_BUCKET)).thenReturn(bucket);
        when(collectionSpec.scopeName()).thenReturn(TEST_SCOPE);
        when(collectionSpec.name()).thenReturn("UN_EXISTS");

        assertThat(statement.isTrue(connection)).isFalse();
    }
}