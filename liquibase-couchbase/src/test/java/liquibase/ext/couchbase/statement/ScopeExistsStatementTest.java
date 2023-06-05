package liquibase.ext.couchbase.statement;

import java.util.Collections;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScopeExistsStatementTest {

    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final Bucket bucket = mock(Bucket.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final ScopeSpec scopeSpec = mock(ScopeSpec.class);

    @BeforeEach
    public void configure() {
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(cluster.bucket(TEST_BUCKET)).thenReturn(bucket);
        when(bucket.collections()).thenReturn(collectionManager);
        when(collectionManager.getAllScopes()).thenReturn(Collections.singletonList(scopeSpec));
    }

    @Test
    void Should_return_true_if_scope_exists() {
        ScopeExistsStatement statement = new ScopeExistsStatement(TEST_BUCKET, TEST_SCOPE);

        when(scopeSpec.name()).thenReturn(TEST_SCOPE);

        assertThat(statement.isTrue(connection)).isTrue();
    }

    @Test
    void Should_return_false_if_scope_not_found() {
        ScopeExistsStatement statement = new ScopeExistsStatement(TEST_BUCKET, TEST_SCOPE);

        when(scopeSpec.name()).thenReturn("another scope");

        assertThat(statement.isTrue(connection)).isFalse();
    }
}