package system.change;

import common.matchers.CouchbaseClusterAssert;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.statement.ScopeExistsStatement;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.DROP_NON_EXISTING_SCOPE_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_NON_EXISTING_SCOPE_MARK_RUN_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_SCOPE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE_DELETE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class DropScopeSystemTest extends LiquibaseSystemTest {

    @AfterEach
    void cleanUpd() {
        ScopeExistsStatement scopeExistsStatement = new ScopeExistsStatement(TEST_BUCKET, TEST_SCOPE_DELETE);
        if (scopeExistsStatement.isTrue(database.getConnection())) {
            bucketOperator.dropScope(TEST_SCOPE_DELETE);
        }
    }

    @Test
    @SneakyThrows
    void Scope_should_be_deleted() {
        bucketOperator.createScope(TEST_SCOPE_DELETE);
        Liquibase liquibase = liquibase(DROP_SCOPE_TEST_XML);
        liquibase.update();

        CouchbaseClusterAssert.assertThat(cluster).hasNoScope(TEST_SCOPE_DELETE, TEST_BUCKET);
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_scope_should_be_mark_as_run_precondition() {
        Liquibase liquibase = liquibase(DROP_NON_EXISTING_SCOPE_MARK_RUN_TEST_XML);
        liquibase.update();
        assertDoesNotThrow(() -> liquibase.update());
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_scope_should_throw_exception_precondition() {
        Liquibase liquibase = liquibase(DROP_NON_EXISTING_SCOPE_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update)
                .withMessageContaining("Scope %s does not exist in bucket %s", "testScopeNotExist", TEST_BUCKET);
    }

}
