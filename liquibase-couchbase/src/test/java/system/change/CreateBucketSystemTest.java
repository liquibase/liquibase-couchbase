package system.change;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.exception.InvalidJSONException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_INVALID_CHANGELOG_TEST_JSON;
import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_TEST_JSON;
import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_TEST_YAML;
import static common.constants.TestConstants.CREATE_BUCKET_SYSTEM_TEST_NAME;
import static common.matchers.CouchbaseClusterAssert.assertThat;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CreateBucketSystemTest extends LiquibaseSystemTest {

    @AfterEach
    void cleanUpd() {
        if (clusterOperator.isBucketExists(CREATE_BUCKET_SYSTEM_TEST_NAME)) {
            clusterOperator.dropBucket(CREATE_BUCKET_SYSTEM_TEST_NAME);
        }
    }

    @Test
    @SneakyThrows
    void Bucket_should_be_created_and_rolled_back() {
        Liquibase liquibase = liquibase(CREATE_BUCKET_TEST_XML);

        liquibase.update();
        assertThat(cluster).hasBucket(CREATE_BUCKET_SYSTEM_TEST_NAME);

        liquibase.rollback(1, null);
        assertThat(cluster).hasNoBucket(CREATE_BUCKET_SYSTEM_TEST_NAME);
    }

    @Test
    @SneakyThrows
    void Bucket_create_changelog_should_be_ignored_when_exist() {
        Liquibase liquibase = liquibase(CREATE_DUPLICATE_BUCKET_TEST_XML);
        liquibase.update();
        CouchbaseClusterAssert.assertThat(cluster).hasBucket(TEST_BUCKET);
    }

    @Test
    @SneakyThrows
    void Bucket_should_be_created_json() {
        Liquibase liquibase = liquibase(CREATE_BUCKET_TEST_JSON);
        liquibase.update();
        assertThat(cluster).hasBucket(CREATE_BUCKET_SYSTEM_TEST_NAME);
    }

    @Test
    @SneakyThrows
    void Bucket_should_be_created_yaml() {
        Liquibase liquibase = liquibase(CREATE_BUCKET_TEST_YAML);
        liquibase.update();
        assertThat(cluster).hasBucket(CREATE_BUCKET_SYSTEM_TEST_NAME);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_invalid_json_changelog() {
        Liquibase liquibase = liquibase(CREATE_BUCKET_INVALID_CHANGELOG_TEST_JSON);
        InvalidJSONException invalidJSONException = new InvalidJSONException(CREATE_BUCKET_INVALID_CHANGELOG_TEST_JSON);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update)
                .withCause(invalidJSONException);
    }
}
