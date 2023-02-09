package integration.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import com.couchbase.client.java.manager.query.DropQueryIndexOptions;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.CreateQueryIndexStatement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static common.constants.TestConstants.COMPOUND_INDEX;
import static common.constants.TestConstants.FIELD_1;
import static common.constants.TestConstants.FIELD_2;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.MANUALLY_CREATED_INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_CONTENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateQueryIndexStatementIT extends BucketTestCase {

    @BeforeEach
    void localSetUp() {
        cluster.bucket(TEST_BUCKET)
                .defaultCollection()
                .insert(TEST_ID, TEST_CONTENT);
    }

    @AfterEach
    void cleanUp() {
        cluster.bucket(TEST_BUCKET).defaultCollection().remove(TEST_ID);
        DropQueryIndexOptions options = DropQueryIndexOptions.dropQueryIndexOptions().ignoreIfNotExists(true);
        cluster.queryIndexes().dropIndex(TEST_BUCKET, MANUALLY_CREATED_INDEX, options);
        cluster.queryIndexes().dropIndex(TEST_BUCKET, COMPOUND_INDEX, options);
        cluster.queryIndexes().dropIndex(TEST_BUCKET, INDEX, options);
    }

    @Test
    void Should_create_index_when_index_does_not_exist() {
        createStatementForSecondaryIndex().execute(database.getConnection());
        assertThat(cluster).queryIndexes(TEST_BUCKET).hasQueryIndexForName(INDEX);
    }

    @Test
    void Should_ignore_index_creation_with_the_same_name() {
        cluster.queryIndexes().createIndex(TEST_BUCKET, INDEX, Collections.singletonList(FIELD_2));
        createStatementForSecondaryIndex().execute(database.getConnection());
        assertEquals(1, cluster.queryIndexes().getAllIndexes(TEST_BUCKET).size());
        //check that the index target column hasn't been overridden
        String indexTargetField = (String)cluster.queryIndexes().getAllIndexes(TEST_BUCKET).get(0).indexKey().get(0);
        assertEquals("`" + FIELD_2 + "`", indexTargetField);
    }

    @Test
    void Should_create_index_in_the_custom_namespace() {
        CreateQueryIndexStatement statement = new CreateQueryIndexStatement(TEST_BUCKET
                , INDEX
                , Arrays.asList(FIELD_1, FIELD_2)
                , customOptions()
        );
        statement.execute(database.getConnection());
        assertThat(cluster).queryIndexes(TEST_BUCKET).hasQueryIndexForName(INDEX);
    }

    @Test
    void Should_create_compound_index() {
        createStatementForCompoundSecondaryIndex().execute(database.getConnection());
        assertThat(cluster).queryIndexes(TEST_BUCKET).hasQueryIndexForName(COMPOUND_INDEX);
    }

    private CreateQueryIndexStatement createStatementForSecondaryIndex() {
        return new CreateQueryIndexStatement(
                TEST_BUCKET,
                INDEX,
                Collections.singletonList(FIELD_1),
                indexOptions()
        );
    }

    private CreateQueryIndexStatement createStatementForCompoundSecondaryIndex() {
        return new CreateQueryIndexStatement(
                TEST_BUCKET,
                COMPOUND_INDEX,
                Arrays.asList(FIELD_1, FIELD_2),
                indexOptions()
        );
    }

    private CreateQueryIndexOptions indexOptions() {
        return CreateQueryIndexOptions.createQueryIndexOptions()
                .numReplicas(0)
                .deferred(true)
                .ignoreIfExists(true);
    }

    private CreateQueryIndexOptions customOptions() {
        return CreateQueryIndexOptions.createQueryIndexOptions()
                .scopeName(TEST_SCOPE)
                .collectionName(TEST_COLLECTION)
                .numReplicas(0)
                .deferred(true)
                .ignoreIfExists(true);
    }
}
