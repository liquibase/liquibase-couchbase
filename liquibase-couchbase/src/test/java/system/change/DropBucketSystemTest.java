package system.change;

import common.matchers.CouchbaseClusterAssert;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_MARK_RUN_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_XML;
import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.matchers.CouchbaseDbAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DropBucketSystemTest extends LiquibaseSystemTest {

    @AfterEach
    void cleanUpd() {
        if (clusterOperator.isBucketExists(NEW_TEST_BUCKET)) {
            clusterOperator.dropBucket(NEW_TEST_BUCKET);
        }
    }

    private void createDeletingBucket() {
        clusterOperator.createBucket(NEW_TEST_BUCKET);
    }

    @Test
    @SneakyThrows
    void Bucket_should_be_deleted() {
        createDeletingBucket();
        Liquibase liquibase = liquibase(DROP_BUCKET_TEST_XML);
        liquibase.update();
        CouchbaseClusterAssert.assertThat(cluster).hasNoBucket(NEW_TEST_BUCKET);
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_bucket_should_be_ignored_precondition() {
        Liquibase liquibase = liquibase(DROP_BUCKET_MARK_RUN_TEST_XML);
        assertDoesNotThrow(() -> liquibase.update());
        assertThat(database).lastChangeLogHasExecStatus(ChangeSet.ExecType.MARK_RAN);
    }

}
