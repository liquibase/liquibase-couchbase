package integration.statement;

import org.junit.jupiter.api.Test;

import liquibase.ext.couchbase.statement.ScopeExistsStatement;
import common.BucketTestCase;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;

class ScopeExistsStatementIT extends BucketTestCase {

    @Test
    void Should_return_true_when_scope_exists() {
        ScopeExistsStatement statement = new ScopeExistsStatement(TEST_BUCKET, TEST_SCOPE);

        assertThat(statement.isScopeExists(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_scope_doesnt_exists() {
        ScopeExistsStatement statement = new ScopeExistsStatement(TEST_BUCKET, "notCreatedScope");

        assertThat(statement.isScopeExists(database.getConnection())).isFalse();
    }
}
