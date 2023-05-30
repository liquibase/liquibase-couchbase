package system.change;

import common.matchers.CouchbaseDbAssert;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_DUPLICATE_FAIL_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_DUPLICATE_IGNORE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CreateCollectionSystemTest extends LiquibaseSystemTest {

    private final String travelsCollection = "travels";

    @BeforeEach
    void cleanUp() {
        if (bucketOperator.hasCollectionInScope(travelsCollection, TEST_SCOPE)) {
            bucketOperator.dropCollection(travelsCollection, TEST_SCOPE);
        }
    }

    @Test
    @SneakyThrows
    void Collection_should_be_created_and_rolled_back() {
        Liquibase liquibase = liquibase(CREATE_COLLECTION_TEST_XML);

        liquibase.update();
        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope(travelsCollection, TEST_SCOPE);

        liquibase.rollback(1, null);
        assertThat(cluster.bucket(TEST_BUCKET)).hasNoCollectionInScope(travelsCollection, TEST_SCOPE);
    }

    @Test
    @SneakyThrows
    void Collection_should_not_be_created_if_duplicate_precondition_mark_run() {
        Liquibase liquibase = liquibase(CREATE_COLLECTION_DUPLICATE_IGNORE_TEST_XML);

        liquibase.update();

        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope(travelsCollection, TEST_SCOPE);
        CouchbaseDbAssert.assertThat(database).lastChangeLogHasExecStatus(ChangeSet.ExecType.MARK_RAN);
    }

    @Test
    @SneakyThrows
    void Error_when_try_create_duplicate_collection_without_predicate() {
        Liquibase liquibase = liquibase(CREATE_COLLECTION_DUPLICATE_FAIL_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);
    }
}
