package liquibase.integration.statement;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;

import org.junit.jupiter.api.Test;

import liquibase.ext.statement.DropPrimaryIndexStatement;
import liquibase.integration.BucketTestCase;
import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static liquibase.common.matchers.CouchBaseClusterAssert.assertThat;


class DropPrimaryIndexStatementTest extends BucketTestCase {

    private static final String TEST_ID = "id1";
    private static final String TEST_CONTENT = "{ \"name\":\"user\", \"type\":\"customer\" }";
    private Bucket bucket = cluster.bucket(TEST_BUCKET);

    /**
     * Primary index is created by default on test collection
     */
    @Test
    void Should_drop_Primary_index() {
        cluster.queryIndexes().createPrimaryIndex(TEST_BUCKET);

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_BUCKET, null, null);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHavePrimary();
    }

    @Test
    void Should_drop_primary_index_for_specific_keyspace() {
        CreatePrimaryQueryIndexOptions options = createPrimaryQueryIndexOptions()
                .scopeName(TEST_SCOPE)
                .collectionName(TEST_COLLECTION);
        cluster.queryIndexes().createPrimaryIndex(TEST_BUCKET, options);

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_BUCKET, TEST_COLLECTION, TEST_SCOPE);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHavePrimary();
    }
}