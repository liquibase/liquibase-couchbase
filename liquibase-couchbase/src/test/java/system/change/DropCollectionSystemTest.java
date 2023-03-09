package system.change;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquiBaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.DROP_COLLECTION_IN_NOT_CREATED_BUCKET_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_COLLECTION_IN_NOT_CREATED_SCOPE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_EXISTING_COLLECTION_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.SKIP_DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DropCollectionSystemTest extends LiquiBaseSystemTest {
    @Test
    @SneakyThrows
    void Should_drop_collection_when_exists() {
        String collectionName = "dropExistingCollection";
        bucketOperator.createCollection(collectionName, TEST_SCOPE);

        Liquibase liquibase = liquiBase(DROP_EXISTING_COLLECTION_TEST_XML);

        liquibase.update();

        assertThat(cluster.bucket(TEST_BUCKET)).hasNoCollectionInScope(collectionName, TEST_SCOPE);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_bucket_not_exists() {
        Liquibase liquibase = liquiBase(DROP_COLLECTION_IN_NOT_CREATED_BUCKET_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_scope_not_exists() {
        Liquibase liquibase = liquiBase(DROP_COLLECTION_IN_NOT_CREATED_SCOPE_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_collection_not_exists() {
        Liquibase liquibase = liquiBase(DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);
    }

    @Test
    @SneakyThrows
    void Should_skip_when_collection_not_exists() {
        Liquibase liquibase = liquiBase(SKIP_DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML);

        assertDoesNotThrow(() -> liquibase.update());
    }

}