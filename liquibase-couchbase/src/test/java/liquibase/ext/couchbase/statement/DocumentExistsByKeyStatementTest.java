package liquibase.ext.couchbase.statement;

import java.util.Collections;
import java.util.HashSet;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DocumentExistsByKeyStatementTest {

    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Scope scope = mock(Scope.class);
    private final Collection collection = mock(Collection.class);
    private final BucketSettings bucketSettings = mock(BucketSettings.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final ScopeSpec scopeSpec = mock(ScopeSpec.class);
    private final CollectionSpec collectionSpec = mock(CollectionSpec.class);


    @BeforeEach
    public void configure() {
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(cluster.bucket(TEST_KEYSPACE.getBucket())).thenReturn(bucket);
        when(bucketManager.getBucket(TEST_KEYSPACE.getBucket())).thenReturn(bucketSettings);
        when(bucket.scope(TEST_KEYSPACE.getScope())).thenReturn(scope);
        when(bucket.collections()).thenReturn(collectionManager);
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
        when(scopeSpec.collections()).thenReturn(new HashSet<>(Collections.singletonList(collectionSpec)));
        when(scope.collection(TEST_KEYSPACE.getCollection())).thenReturn(collection);
    }

    @Test
    void Should_return_true_if_document_key_exists() {
        String key = "key";

        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, key);

        when(collectionSpec.scopeName()).thenReturn(TEST_KEYSPACE.getScope());
        when(collectionSpec.name()).thenReturn(TEST_KEYSPACE.getCollection());

        ExistsResult existsResult = mock(ExistsResult.class);

        when(collection.exists(key)).thenReturn(existsResult);
        when(existsResult.exists()).thenReturn(true);

        assertThat(statement.isTrue(connection)).isTrue();

        verify(existsResult).exists();
    }

    @Test
    void Should_return_false_if_document_key_not_exists() {
        String key = "key";

        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, key);

        when(collectionSpec.scopeName()).thenReturn(TEST_KEYSPACE.getScope());
        when(collectionSpec.name()).thenReturn(TEST_KEYSPACE.getCollection());

        ExistsResult existsResult = mock(ExistsResult.class);

        when(collection.exists(key)).thenReturn(existsResult);
        when(existsResult.exists()).thenReturn(false);

        assertThat(statement.isTrue(connection)).isFalse();

        verify(existsResult).exists();
    }

    @Test
    void Should_return_false_if_collection_not_found() {
        String key = "key";

        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, key);

        when(collectionSpec.scopeName()).thenReturn("another scope");
        when(collectionSpec.name()).thenReturn("another collection");

        ExistsResult existsResult = mock(ExistsResult.class);

        when(collection.exists(key)).thenReturn(existsResult);
        when(existsResult.exists()).thenReturn(false);

        assertThat(statement.isTrue(connection)).isFalse();

        verify(existsResult, never()).exists();
    }
}