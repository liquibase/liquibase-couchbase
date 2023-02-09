package integration.statement;

import com.couchbase.client.core.error.IndexExistsException;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.CreatePrimaryQueryIndexStatement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.MANUALLY_CREATED_INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_CONTENT;
import static common.constants.TestConstants.TEST_ID;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatePrimaryQueryIndexStatementIT extends BucketTestCase {

    @BeforeEach
    void localSetUp() {
        cluster.bucket(TEST_BUCKET)
                .defaultCollection()
                .insert(TEST_ID, TEST_CONTENT);
    }

    @AfterEach
    void cleanUp() {
        cluster.bucket(TEST_BUCKET).defaultCollection().remove(TEST_ID);
        DropQueryIndexOptions options = DropQueryIndexOptions
                .dropQueryIndexOptions()
                .ignoreIfNotExists(true);
        cluster.queryIndexes().dropIndex(TEST_BUCKET, INDEX, options);
        cluster.queryIndexes().dropIndex(TEST_BUCKET, MANUALLY_CREATED_INDEX, options);
    }

    @Test
    void Should_create_primary_index_when_primary_index_does_not_exist() {
        CreatePrimaryQueryIndexStatement statement =
                new CreatePrimaryQueryIndexStatement(TEST_BUCKET, createOptions());
        statement.execute(database.getConnection());
        assertThat(cluster).queryIndexes(TEST_BUCKET).hasPrimaryIndexForName(INDEX);
    }

    @Test
    void Should_ignore_primary_index_creation_if_primary_index_exists() {
        createPrimaryIndexManually();
        CreatePrimaryQueryIndexStatement statement =
                new CreatePrimaryQueryIndexStatement(TEST_BUCKET, createOptions());
        statement.execute(database.getConnection());
        assertEquals(MANUALLY_CREATED_INDEX,
                cluster.queryIndexes().getAllIndexes(TEST_BUCKET).get(0).name());
    }

    @Test
    void Should_throw_an_exception_when_creating_second_primary_index_in_the_same_keyspace() {
        createPrimaryIndexManually();
        CreatePrimaryQueryIndexStatement statement = new CreatePrimaryQueryIndexStatement(TEST_BUCKET,
                createOptions().ignoreIfExists(false));
        try {
            statement.execute(database.getConnection());
        } catch (IndexExistsException e) {
            assertThat(e.getClass()).isExactlyInstanceOf(IndexExistsException.class);
        }
    }

    private CreatePrimaryQueryIndexOptions createOptions() {
        return CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .numReplicas(0)
                .indexName(INDEX)
                .ignoreIfExists(true);
    }

    private void createPrimaryIndexManually() {
        CreatePrimaryQueryIndexOptions options = CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(MANUALLY_CREATED_INDEX);
        cluster.queryIndexes().createPrimaryIndex(TEST_BUCKET, options);
    }
}
