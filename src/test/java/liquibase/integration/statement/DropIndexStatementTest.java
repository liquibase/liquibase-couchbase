package liquibase.integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import com.wdt.couchbase.Keyspace;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.ext.statement.DropIndexStatement;
import liquibase.integration.BucketTestCase;
import static com.couchbase.client.java.manager.query.CreateQueryIndexOptions.createQueryIndexOptions;
import static com.wdt.couchbase.Keyspace.keyspace;
import static java.util.Collections.singletonList;
import static liquibase.common.matchers.CouchBaseClusterAssert.assertThat;


class DropIndexStatementTest extends BucketTestCase {
    private static final String TEST_ID = "id1";
    private static final String TEST_CONTENT = "{ \"name\":\"user\", \"type\":\"customer\" }";
    private final String indexName = "testIndex";
    private Bucket bucket;

    @BeforeEach
    void localSetUp() {
        bucket = cluster.bucket(TEST_BUCKET);
        bucket.defaultCollection().insert(TEST_ID, TEST_CONTENT);
    }

    @AfterEach
    void cleanUp() {
        bucket.defaultCollection().remove(TEST_ID);
    }

    @Test
    void Should_drop_existing_index_in_default_scope() {
        createIndexInDefaultScope(TEST_BUCKET, indexName);

        DropIndexStatement statement = new DropIndexStatement(this.indexName, TEST_BUCKET, null, null);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHave(indexName);
    }

    @Test
    void Should_drop_index_for_specific_keyspace() {
        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
        createIndex(keyspace, indexName);

        DropIndexStatement statement = new DropIndexStatement(indexName, TEST_BUCKET, TEST_COLLECTION, TEST_SCOPE);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHave(indexName);
    }

    private void createIndexInDefaultScope(String bucket, String indexName) {
        cluster.queryIndexes().createIndex(bucket, indexName, singletonList("name"));
    }

    private void createIndex(Keyspace keyspace, String indexName) {
        CreateQueryIndexOptions options = createQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
        cluster.queryIndexes()
                .createIndex(keyspace.getBucket(), indexName, singletonList("name"), options);
    }

}