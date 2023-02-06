package liquibase.integration.precondition;

import com.wdt.couchbase.exception.ScopeNotExistsPreconditionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.ext.precondition.ScopeExistsPrecondition;
import liquibase.integration.BucketTestCase;
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
