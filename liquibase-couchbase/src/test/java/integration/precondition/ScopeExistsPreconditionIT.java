package integration.precondition;

import liquibase.ext.couchbase.exception.ScopeNotExistsPreconditionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.ext.couchbase.precondition.ScopeExistsPrecondition;
import common.BucketTestCase;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ScopeExistsPreconditionIT extends BucketTestCase {

    private static final ScopeExistsPrecondition precondition = new ScopeExistsPrecondition();

    @BeforeEach
    void setUpLocal() {
        precondition.setBucketName(TEST_BUCKET);
    }

    @Test
    void Should_not_throws_when_scope_exists() {
        precondition.setScopeName(TEST_SCOPE);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_exception_when_scope_doesnt_exists() {
        String notExistedScope = "notExistedScope";
        precondition.setScopeName(notExistedScope);

        assertThatExceptionOfType(ScopeNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Scope %s does not exist in bucket %s", notExistedScope, TEST_BUCKET);
    }
}
