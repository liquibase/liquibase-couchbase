package integration.statement;

import com.couchbase.client.core.error.ScopeExistsException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.CreateScopeStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class CreateScopeStatementTest extends RandomizedScopeTestCase {

    @Test
    void Should_create_scope() {
        CreateScopeStatement createScopeStatement = new CreateScopeStatement(TEST_SCOPE, bucketName);

        createScopeStatement.execute(database.getConnection());

        assertThat(createScopeStatement.getScopeName()).isEqualTo(TEST_SCOPE);
    }

    @Test
    void Should_throw_exception_if_scope_exists() {
        CreateScopeStatement statement = new CreateScopeStatement(scopeName, bucketName);

        assertThatExceptionOfType(ScopeExistsException.class)
            .isThrownBy(() -> statement.execute(database.getConnection()))
                .withMessage("Scope [%s] already exists.", scopeName);
    }

}