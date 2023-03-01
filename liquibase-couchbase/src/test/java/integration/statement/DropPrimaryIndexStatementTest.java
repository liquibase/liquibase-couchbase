package integration.statement;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.DropPrimaryIndexStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.Test;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

class DropPrimaryIndexStatementTest extends RandomizedScopeTestCase {
    private Keyspace keyspace;

    @Test
    void Should_drop_Primary_index() {
        clusterOperator.createPrimaryIndex(bucketName);
        keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(keyspace);

        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).doesNotHavePrimary();
    }

    @Test
    void Should_drop_primary_index_for_specific_keyspace() throws InterruptedException {
        CreatePrimaryQueryIndexOptions options = createPrimaryQueryIndexOptions()
                .scopeName(scopeName)
                .collectionName(collectionName);
        clusterOperator.createPrimaryIndex(bucketName, options);
        keyspace = keyspace(bucketName, scopeName, collectionName);

        DropPrimaryIndexStatement statement = new DropPrimaryIndexStatement(keyspace);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(bucketName).doesNotHavePrimary();
    }
}