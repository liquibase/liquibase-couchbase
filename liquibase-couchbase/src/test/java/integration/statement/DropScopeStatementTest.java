package integration.statement;

import com.couchbase.client.core.error.ScopeNotFoundException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.DropScopeStatement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class DropScopeStatementTest extends RandomizedScopeTestCase {

    @Test
    void Should_drop_scope() {
        DropScopeStatement dropScopeStatement = new DropScopeStatement(scopeName, bucketName);

        dropScopeStatement.execute(clusterOperator);

        assertThat(clusterOperator.getBucketOperator(bucketName).hasScope(scopeName)).isFalse();
        bucketOperator.getOrCreateScope(scopeName);
    }

    @Test
    void Should_throw_scope_not_found() {
        String scope = "notExistingScope";
        DropScopeStatement dropScopeStatement = new DropScopeStatement(scope, bucketName);

        assertThatExceptionOfType(ScopeNotFoundException.class)
                .isThrownBy(() -> dropScopeStatement.execute(clusterOperator))
                .withMessage("Scope [%s] not found.", scope);
    }

}