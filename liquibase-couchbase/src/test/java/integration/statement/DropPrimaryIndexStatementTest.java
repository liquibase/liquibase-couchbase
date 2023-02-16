package integration.statement;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.BucketTestCase;
import common.operators.TestClusterOperator;
import liquibase.ext.couchbase.statement.DropPrimaryIndexStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;


class DropPrimaryIndexStatementTest extends BucketTestCase {
    private TestClusterOperator clusterOperator;

    @BeforeEach
    public void setUp() {
        clusterOperator = new TestClusterOperator(cluster);
    }

    @Test
    void Should_drop_Primary_index() {
        clusterOperator.createPrimaryIndex(TEST_BUCKET);
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(keyspace);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHavePrimary();
    }

    @Test
    void Should_drop_primary_index_for_specific_keyspace() {
        CreatePrimaryQueryIndexOptions options = createPrimaryQueryIndexOptions()
                .scopeName(TEST_SCOPE)
                .collectionName(TEST_COLLECTION);
        clusterOperator.createPrimaryIndex(TEST_BUCKET, options);

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(TEST_KEYSPACE);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHavePrimary();
    }
}