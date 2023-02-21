package integration.statement;

import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.ScopeExistsStatement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScopeExistsStatementIT extends RandomizedScopeTestCase {
    @Test
    void Should_return_true_when_scope_exists() {
        ScopeExistsStatement statement = new ScopeExistsStatement(bucketName, scopeName);

        assertThat(statement.isScopeExists(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_scope_doesnt_exists() {
        ScopeExistsStatement statement = new ScopeExistsStatement(bucketName, "notCreatedScope");

        assertThat(statement.isScopeExists(database.getConnection())).isFalse();
    }
}
