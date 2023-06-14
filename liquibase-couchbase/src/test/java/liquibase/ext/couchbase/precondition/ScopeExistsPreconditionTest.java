package liquibase.ext.couchbase.precondition;

import com.couchbase.client.core.deps.com.google.common.collect.Lists;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.database.Database;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.exception.precondition.ScopeNotExistsPreconditionException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.constants.TestConstants.TEST_SCOPE_DELETE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScopeExistsPreconditionTest {

    private final Database database = mock(Database.class);
    private final CouchbaseConnection connection = mock(CouchbaseConnection.class);
    private final Cluster cluster = mock(Cluster.class);
    private final BucketManager bucketManager = mock(BucketManager.class);
    private final CollectionManager collectionManager = mock(CollectionManager.class);
    private final Bucket bucket = mock(Bucket.class);

    @BeforeEach
    public void configure() {
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.buckets()).thenReturn(bucketManager);
        when(cluster.bucket(NEW_TEST_BUCKET)).thenReturn(bucket);
        when(bucket.collections()).thenReturn(collectionManager);
    }

    @Test
    @SneakyThrows
    void Should_pass_when_scope_exists() {
        ScopeExistsPrecondition precondition = createScopePrecondition(TEST_SCOPE, NEW_TEST_BUCKET);
        when(collectionManager.getAllScopes()).thenReturn(Lists.newArrayList(ScopeSpec.create(TEST_SCOPE)));

        precondition.check(database, null, null, null);
    }

    @Test
    @SneakyThrows
    void Should_throw_exception_when_scope_not_exists() {
        ScopeExistsPrecondition precondition = createScopePrecondition(TEST_SCOPE_DELETE, NEW_TEST_BUCKET);

        assertThatExceptionOfType(ScopeNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Scope %s does not exist in bucket %s", precondition.getScopeName(), precondition.getBucketName());
    }

    private ScopeExistsPrecondition createScopePrecondition(String scopeName, String bucketName) {
        ScopeExistsPrecondition precondition = new ScopeExistsPrecondition();
        precondition.setScopeName(scopeName);
        precondition.setBucketName(bucketName);

        return precondition;
    }
}