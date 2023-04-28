package system.change;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.matchers.CouchbaseClusterAssert;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static common.constants.ChangeLogSampleFilePaths.DROP_INDEX_SYSTEM_TEST_MARK_RUN_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_INDEX_SYSTEM_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_NON_EXISTING_INDEX_SYSTEM_TEST_ERROR_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_PRIMARY_INDEX_BY_NAME_SYSTEM_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_PRIMARY_INDEX_SYSTEM_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseDbAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DropIndexSystemTest extends LiquibaseSystemTest {

    private final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    private final String docId1 = "newTestDoc_ID1";
    private final String docId1IndexName = "doc1_index";
    private final Document doc1 = collectionOperator.generateTestDocById(docId1);

    @AfterEach
    void cleanUpd() {
        if (collectionOperator.collectionIndexExists(docId1IndexName)) {
            collectionOperator.dropIndex(docId1IndexName);
        }
        if (collectionOperator.docExists(docId1)) {
            collectionOperator.removeDoc(doc1);
        }
    }

    private void createDeletingIndex() {
        collectionOperator.insertDocs(doc1);
        collectionOperator.createQueryIndex(docId1IndexName, doc1.getFields(), null);
    }

    private void createDeletingPrimaryIndex(CreatePrimaryQueryIndexOptions options) {
        if (options == null) {
            collectionOperator.createPrimaryIndex();
            return;
        }
        collectionOperator.createPrimaryIndex(options);
    }

    @Test
    @SneakyThrows
    void Index_should_be_deleted() {
        createDeletingIndex();
        Liquibase liquibase = liquibase(DROP_INDEX_SYSTEM_TEST_XML);
        liquibase.update();
        CouchbaseClusterAssert.assertThat(cluster).queryIndexes(TEST_KEYSPACE).doesNotHave(docId1IndexName);
    }

    @Test
    @SneakyThrows
    void Primary_index_should_be_deleted() {
        createDeletingPrimaryIndex(null);
        Liquibase liquibase = liquibase(DROP_PRIMARY_INDEX_SYSTEM_TEST_XML);
        liquibase.update();
        CouchbaseClusterAssert.assertThat(cluster).queryIndexes(TEST_KEYSPACE).doesNotHave(docId1IndexName);
    }

    @Test
    @SneakyThrows
    void Primary_index_should_be_deleted_by_name() {
        createDeletingPrimaryIndex(createPrimaryQueryIndexOptions().indexName(docId1IndexName));
        Liquibase liquibase = liquibase(DROP_PRIMARY_INDEX_BY_NAME_SYSTEM_TEST_XML);
        liquibase.update();
        CouchbaseClusterAssert.assertThat(cluster).queryIndexes(TEST_KEYSPACE).doesNotHave(docId1IndexName);
    }

    @Test
    @SneakyThrows
    void Index_should_not_be_deleted_mark_as_read_precondition() {
        Liquibase liquibase = liquibase(DROP_INDEX_SYSTEM_TEST_MARK_RUN_XML);
        assertDoesNotThrow(() -> liquibase.update());
        assertThat(database).lastChangeLogHasExecStatus(ChangeSet.ExecType.MARK_RAN);
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_index_should_throw_exception_precondition() {
        Liquibase liquibase = liquibase(DROP_NON_EXISTING_INDEX_SYSTEM_TEST_ERROR_XML);
        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update)
                .withMessageContaining("Index %s(bucket name - %s, scope name - %s, collection - %s) does not exist",
                        docId1IndexName, TEST_BUCKET, TEST_SCOPE, null);
    }

}
