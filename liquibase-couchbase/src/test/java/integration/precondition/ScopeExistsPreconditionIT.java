package integration.precondition;

import common.RandomizedScopeTestCase;
import common.operators.TestBucketOperator;
import liquibase.ext.couchbase.exception.precondition.ScopeNotExistsPreconditionException;
import liquibase.ext.couchbase.precondition.ScopeExistsPrecondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ScopeExistsPreconditionIT extends RandomizedScopeTestCase {

    private static final ScopeExistsPrecondition precondition = new ScopeExistsPrecondition();

    @BeforeEach
    void setUpLocal() {
        precondition.setBucketName(bucketName);
    }

    @Test
    void Should_not_throw_when_scope_exists() {
        precondition.setScopeName(scopeName);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_exception_when_scope_doesnt_exists() {
        String notExistedScope = "notExistedScope";
        precondition.setScopeName(notExistedScope);

        assertThatExceptionOfType(ScopeNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Scope %s does not exist in bucket %s", notExistedScope, bucketName);
    }
}
