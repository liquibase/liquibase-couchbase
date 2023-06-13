package liquibase.ext.couchbase.precondition;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.CollectionNotExistsPreconditionException;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.serializer.LiquibaseSerializable.GENERIC_CHANGELOG_EXTENSION_NAMESPACE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CollectionExistsPreconditionTest {
    private final Database database = mock(Database.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final Bucket bucket = mock(Bucket.class);

    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(cluster.bucket(NEW_TEST_BUCKET)).thenReturn(bucket);
        when(clusterOperator.getBucketOperator(NEW_TEST_BUCKET)).thenReturn(bucketOperator);
        when(bucket.collections()).thenReturn(collectionManager);
    }

    @Test
    @SneakyThrows
    void Should_pass_when_collection_exists() {
        CollectionExistsPrecondition precondition = new CollectionExistsPrecondition(NEW_TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
        BucketSettings settings = BucketSettings.create(NEW_TEST_BUCKET);
        when(bucketManager.getBucket(NEW_TEST_BUCKET)).thenReturn(settings);
        when(collectionManager.getAllScopes()).thenReturn(
                Lists.newArrayList(ScopeSpec.create("a", Sets.newHashSet(CollectionSpec.create(TEST_COLLECTION, TEST_SCOPE)))));

        precondition.check(database, null, null, null);
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_collection_not_exists() {
        CollectionExistsPrecondition precondition = new CollectionExistsPrecondition();
        precondition.setScopeName(TEST_SCOPE);
        precondition.setBucketName(NEW_TEST_BUCKET);
        precondition.setCollectionName(TEST_COLLECTION);
        BucketSettings settings = BucketSettings.create(NEW_TEST_BUCKET);
        when(bucketManager.getBucket(NEW_TEST_BUCKET)).thenReturn(settings);

        assertThatExceptionOfType(CollectionNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Collection %s does not exist in bucket %s in scope %s",
                        precondition.getCollectionName(), precondition.getBucketName(), precondition.getScopeName());
    }

    @Test
    void Should_return_expected_name() {
        CollectionExistsPrecondition precondition = new CollectionExistsPrecondition();

        assertThat(precondition.getName()).isEqualTo("collectionExists");
    }

    @Test
    void Should_return_expected_serialized_object_namespace() {
        CollectionExistsPrecondition precondition = new CollectionExistsPrecondition();

        assertThat(precondition.getSerializedObjectNamespace()).isEqualTo(GENERIC_CHANGELOG_EXTENSION_NAMESPACE);
    }
}