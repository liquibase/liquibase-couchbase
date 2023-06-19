package liquibase.ext.couchbase.precondition;

import com.couchbase.client.core.CoreKeyspace;
import com.couchbase.client.core.api.kv.CoreExistsResult;
import com.couchbase.client.core.deps.com.google.common.collect.Lists;
import com.couchbase.client.core.deps.com.google.common.collect.Sets;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.kv.ExistsResult;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.DocumentNotExistsPreconditionException;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.constants.TestConstants.TEST_SCOPE_DELETE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentExistsByKeyPreconditionTest {

    private final Database database = mock(Database.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final ClusterOperator clusterOperator = mock(ClusterOperator.class);
    private final BucketOperator bucketOperator = mock(BucketOperator.class);
    private final Bucket bucket = mock(Bucket.class);
    private final Scope scope = mock(Scope.class);
    private final Collection collection = mock(Collection.class);

    private final String documentKey = "KEY";

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
    void Should_pass_when_document_exists() {
        DocumentExistsByKeyPrecondition precondition = createDocExistsPreCondition(NEW_TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION,
                documentKey);
        when(collectionManager.getAllScopes()).thenReturn(
                Lists.newArrayList(ScopeSpec.create("a", Sets.newHashSet(CollectionSpec.create(TEST_COLLECTION, TEST_SCOPE)))));

        when(bucket.scope(TEST_SCOPE)).thenReturn(scope);
        when(scope.collection(TEST_COLLECTION)).thenReturn(collection);
        when(collection.exists(documentKey)).thenReturn(ExistsResult.from(
                new CoreExistsResult(null,
                        new CoreKeyspace(precondition.getBucketName(), precondition.getScopeName(), precondition.getCollectionName()),
                        documentKey, 1, true)));

        precondition.check(database, null, null, null);
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_document_not_exists() {
        DocumentExistsByKeyPrecondition precondition = createDocExistsPreCondition(NEW_TEST_BUCKET, TEST_SCOPE_DELETE, TEST_COLLECTION,
                documentKey);
        assertThatExceptionOfType(DocumentNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Key %s does not exist in bucket %s in scope %s and collection %s",
                        precondition.getKey(), precondition.getBucketName(), precondition.getScopeName(), precondition.getCollectionName());
    }

    private DocumentExistsByKeyPrecondition createDocExistsPreCondition(String bucketName,
                                                                        String scopeName,
                                                                        String collectionName,
                                                                        String documentKey) {
        DocumentExistsByKeyPrecondition precondition = new DocumentExistsByKeyPrecondition();
        precondition.setBucketName(bucketName);
        precondition.setScopeName(scopeName);
        precondition.setCollectionName(collectionName);
        precondition.setKey(documentKey);

        return precondition;
    }
}