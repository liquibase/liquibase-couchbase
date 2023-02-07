package integration.statement;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;

import org.junit.jupiter.api.Test;

import liquibase.ext.couchbase.statement.DropPrimaryIndexStatement;
import common.BucketTestCase;
import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;


class DropPrimaryIndexStatementTest extends BucketTestCase {

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