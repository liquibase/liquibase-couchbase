package liquibase.ext.couchbase.precondition;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.query.CollectionQueryIndexManager;
import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import com.google.common.collect.Lists;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.PrimaryIndexNotExistsPreconditionException;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PrimaryIndexExistsPreconditionTest {
    private final Database database = mock(Database.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Scope scope = mock(Scope.class);
    private final Collection collection = mock(Collection.class);
    private final QueryIndex queryIndex = mock(QueryIndex.class);
    private final QueryIndexManager queryIndexManager = mock(QueryIndexManager.class);
    CollectionQueryIndexManager collectionQueryIndexManager = mock(CollectionQueryIndexManager.class);


    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(clusterOperator.getBucketOperator(TEST_BUCKET)).thenReturn(bucketOperator);
    }

    @Test
    @SneakyThrows
    void Should_pass_when_index_exists_in_bucket() {
        PrimaryIndexExistsPrecondition precondition = new PrimaryIndexExistsPrecondition();
        precondition.setBucketName(TEST_BUCKET);
        precondition.setIndexName(INDEX);

        when(cluster.queryIndexes()).thenReturn(queryIndexManager);
        when(queryIndexManager.getAllIndexes(precondition.getBucketName())).thenReturn(Lists.newArrayList(queryIndex));
        when(queryIndex.name()).thenReturn(INDEX);
        when(queryIndex.primary()).thenReturn(Boolean.TRUE);

        precondition.check(database, null, null, null);

        verify(queryIndex).name();
        verify(queryIndex).primary();
        assertThat(precondition.getName()).isEqualTo("doesPrimaryIndexExist");
    }

    @Test
    @SneakyThrows
    void Should_pass_when_index_exists_in_bucket_scope() {
        PrimaryIndexExistsPrecondition precondition = new PrimaryIndexExistsPrecondition();
        precondition.setBucketName(TEST_BUCKET);
        precondition.setIndexName(INDEX);
        precondition.setScopeName(TEST_SCOPE);

        when(cluster.queryIndexes()).thenReturn(queryIndexManager);
        when(queryIndexManager.getAllIndexes(precondition.getBucketName())).thenReturn(Lists.newArrayList(queryIndex));
        when(queryIndex.scopeName()).thenReturn(Optional.of(TEST_SCOPE));
        when(queryIndex.name()).thenReturn(INDEX);
        when(queryIndex.primary()).thenReturn(Boolean.TRUE);

        precondition.check(database, null, null, null);

        verify(queryIndex).scopeName();
        verify(queryIndex).name();
        verify(queryIndex).primary();
        assertThat(precondition.getName()).isEqualTo("doesPrimaryIndexExist");
    }

    @Test
    @SneakyThrows
    void Should_pass_when_index_exists_in_bucket_scope_collection() {
        PrimaryIndexExistsPrecondition precondition = new PrimaryIndexExistsPrecondition(INDEX, TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        when(cluster.buckets()).thenReturn(bucketManager);
        when(cluster.bucket(TEST_BUCKET)).thenReturn(bucket);
        when(bucket.scope(TEST_SCOPE)).thenReturn(scope);
        when(scope.collection(TEST_COLLECTION)).thenReturn(collection);
        when(collection.queryIndexes()).thenReturn(collectionQueryIndexManager);
        when(collectionQueryIndexManager.getAllIndexes()).thenReturn(Lists.newArrayList(queryIndex));
        when(queryIndex.name()).thenReturn(INDEX);
        when(queryIndex.primary()).thenReturn(Boolean.TRUE);

        precondition.check(database, null, null, null);

        verify(queryIndex).name();
        verify(queryIndex).primary();
        assertThat(precondition.getName()).isEqualTo("doesPrimaryIndexExist");
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_index_not_exists() {
        PrimaryIndexExistsPrecondition precondition = new PrimaryIndexExistsPrecondition();
        precondition.setBucketName(TEST_BUCKET);
        precondition.setIndexName(INDEX);

        when(cluster.queryIndexes()).thenReturn(queryIndexManager);

        when(queryIndexManager.getAllIndexes(precondition.getBucketName())).thenReturn(Lists.newArrayList(queryIndex));
        when(queryIndex.name()).thenReturn("index not exist");

        assertThatExceptionOfType(PrimaryIndexNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Primary index %s(bucket name - %s, scope name - %s, collection - %s) does not exist",
                        precondition.getIndexName(), precondition.getBucketName(), precondition.getScopeName(),
                        precondition.getCollectionName());
    }
}