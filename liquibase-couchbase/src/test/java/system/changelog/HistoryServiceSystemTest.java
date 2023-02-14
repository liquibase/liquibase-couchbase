package system.changelog;

import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.exception.ValidationFailedException;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;
import lombok.SneakyThrows;
import system.LiquiBaseSystemTest;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_DUPLICATE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.CHANGELOG_TEST_XML;
import static common.matchers.ChangeLogAssert.assertThat;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class HistoryServiceSystemTest extends LiquiBaseSystemTest {
    private static final String separator = System.lineSeparator();
    private static final ServiceProvider serviceProvider = new ContextServiceProvider(database);
    private static final Scope scope = cluster.bucket(serviceProvider.getServiceBucketName()).scope(DEFAULT_SERVICE_SCOPE);

    @BeforeEach
    void initBeforeEach() {
        dropTestScope();
        createTestScope();
        createTestCollection();
    }

    @BeforeAll
    static void cleanBeforeAllTests() {
        cleanAllChangeLogs();
    }


    @AfterEach
    void cleanChangeLogs() {
        cleanAllChangeLogs();
    }

    private static void cleanAllChangeLogs() {
        QueryOptions queryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        scope.query("DELETE FROM DATABASECHANGELOG", queryOptions);
    }

    @Test
    @SneakyThrows
    void Should_create_2_changelogs() {
        Liquibase liquibase = liquiBase(CHANGELOG_TEST_XML);

        liquibase.update();

        assertThat(scope)
                .hasDocument(changeSet(1))
                .withExecType(ChangeSet.ExecType.EXECUTED)
                .withOrder(1);
        assertThat(scope)
                .hasDocument(changeSet(2))
                .withExecType(ChangeSet.ExecType.EXECUTED)
                .withOrder(2);
    }

    private String changeSet(Integer changeSetNum) {
        return String.format("liquibase/ext/couchbase/changelog/changelog.changelog-test.xml::%s::dmitry", changeSetNum);
    }


    @Test
    @SneakyThrows
    void Should_create_only_2_changelogs_when_repeat_the_same_xml() {
        Liquibase liquibase = liquiBase(CHANGELOG_TEST_XML);

        liquibase.update();
        liquibase.update();

        assertThat(scope).documentsSizeEqualTo(2);
    }

    @Test
    @SneakyThrows
    void Should_throw_duplicate_error_when_changesets_are_equal_and_check_that_collection_exists() {
        Liquibase liquibase = liquiBase(CHANGELOG_DUPLICATE_TEST_XML);

        assertThatExceptionOfType(ValidationFailedException.class)
                .isThrownBy(liquibase::update)
                .withMessage("Validation Failed:%s" +
                        "     1 changesets had duplicate identifiers%s" +
                        "          liquibase/ext/couchbase/changelog/changelog" +
                        ".changelog-duplicate-test.xml::3::dmitry%s", separator, separator, separator);

        assertThat(cluster.bucket(serviceProvider.getServiceBucketName()))
                .hasCollectionInScope(CHANGE_LOG_COLLECTION, scope.name());
    }

}