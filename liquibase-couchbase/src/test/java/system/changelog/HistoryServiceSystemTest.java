package system.changelog;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import common.matchers.CouchbaseCollectionAssert;
import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import liquibase.Liquibase;
import liquibase.exception.ValidationFailedException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_DUPLICATE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_ROLLBACK_BY_COUNT_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_ROLLBACK_BY_TAG_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_TAG_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.ChangeLogAssert.assertThat;
import static common.matchers.CouchbaseBucketAssert.assertThat;
import static liquibase.changelog.ChangeSet.ExecType.EXECUTED;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class HistoryServiceSystemTest extends LiquibaseSystemTest {
    private static final String separator = System.lineSeparator();
    private static final TestClusterOperator clusterOperator = new TestClusterOperator(cluster);
    private static final TestBucketOperator changeLogBucketOperator = clusterOperator.getOrCreateBucketOperator(SERVICE_BUCKET_NAME);
    private static final TestBucketOperator testBucketOperator = clusterOperator.getOrCreateBucketOperator(TEST_BUCKET);
    private Scope serviceScope;
    private Collection testCollection;

    @BeforeEach
    void initBeforeEach() {
        serviceScope = changeLogBucketOperator.getScope(DEFAULT_SERVICE_SCOPE);
        if (changeLogBucketOperator.hasCollectionInScope(CHANGE_LOG_COLLECTION, DEFAULT_SERVICE_SCOPE)) {
            cleanAllChangeLogs();
        }
        if (testBucketOperator.hasScope(TEST_SCOPE)) {
            testBucketOperator.dropScope(TEST_SCOPE);
            testBucketOperator.createDefaultTestScope();
            testBucketOperator.createDefaultTestCollection();
            testCollection = testBucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        }
    }

    @AfterEach
    void cleanChangeLogs() {
        cleanAllChangeLogs();
    }

    private void cleanAllChangeLogs() {
        QueryOptions queryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        serviceScope.query("DELETE FROM DATABASECHANGELOG", queryOptions);
    }

    @Test
    @SneakyThrows
    void Should_create_2_changelogs() {
        Liquibase liquibase = liquibase(CHANGELOG_TEST_XML);

        liquibase.update();

        assertThat(serviceScope)
                .hasDocument(changeSet(1)).withExecType(EXECUTED).withOrder(1)
                .hasDocument(changeSet(2)).withExecType(EXECUTED).withOrder(2);
    }

    private String changeSet(Integer changeSetNum) {
        return String.format("liquibase/ext/couchbase/changelog/changelog.changelog-test.xml::%s::dmitry",
                changeSetNum);
    }


    @Test
    @SneakyThrows
    void Should_create_only_2_changelogs_when_repeat_the_same_xml() {
        Liquibase liquibase = liquibase(CHANGELOG_TEST_XML);

        liquibase.update();
        liquibase.update();

        assertThat(serviceScope).documentsSizeEqualTo(2);
    }

    @Test
    @SneakyThrows
    void Should_throw_duplicate_error_when_changesets_are_equal_and_check_that_collection_exists() {
        Liquibase liquibase = liquibase(CHANGELOG_DUPLICATE_TEST_XML);

        assertThatExceptionOfType(ValidationFailedException.class).isThrownBy(liquibase::update).withMessage(
                "Validation Failed:%s" + "     1 changesets had duplicate identifiers%s" + "          " + "liquibase" + "/ext/couchbase" +
                        "/changelog/changelog" + ".changelog-duplicate-test.xml::3::dmitry%s",
                separator, separator, separator);

        assertThat(changeLogBucketOperator.getBucket()).hasCollectionInScope(CHANGE_LOG_COLLECTION, serviceScope.name());
    }

    @Test
    @SneakyThrows
    void Should_rollback_2_last_changes() {
        Liquibase liquibase = liquibase(CHANGELOG_ROLLBACK_BY_COUNT_TEST_XML);
        liquibase.update();

        assertThat(serviceScope).documentsSizeEqualTo(3);
        CouchbaseCollectionAssert.assertThat(testCollection).containsIds("rollbackCountId1", "rollbackCountId2", "rollbackCountId3");

        liquibase.rollback(2, null);

        assertThat(serviceScope).documentsSizeEqualTo(1);
        CouchbaseCollectionAssert.assertThat(testCollection).containsIds("rollbackCountId1");
    }

    @Test
    @SneakyThrows
    void Should_rollback_2_last_changes_by_tag() {
        Liquibase liquibase = liquibase(CHANGELOG_ROLLBACK_BY_TAG_TEST_XML);
        liquibase.update();

        assertThat(serviceScope).documentsSizeEqualTo(4);
        CouchbaseCollectionAssert.assertThat(testCollection).containsIds("rollbackTagId1", "rollbackTagId2", "rollbackTagId3");

        liquibase.rollback("lastTag1", (String) null);

        assertThat(serviceScope).documentsSizeEqualTo(1);
        CouchbaseCollectionAssert.assertThat(testCollection).containsIds("rollbackTagId1");
    }

    @Test
    @SneakyThrows
    void Should_tag_last_history_changeSet() {
        Liquibase liquibase = liquibase(CHANGELOG_TAG_TEST_XML);

        liquibase.update();
        liquibase.tag("lastTag");

        assertThat(serviceScope).hasDocument("liquibase/ext/couchbase/changelog/changelog.tag-test.xml::tagId1::dmitry.dashko")
                .withTag("lastTag");
    }

}