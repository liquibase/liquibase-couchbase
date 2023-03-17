package integration.statement;

import com.couchbase.client.core.error.ScopeExistsException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.CreateScopeStatement;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class CreateScopeStatementIT extends RandomizedScopeTestCase {
    @Test
    void Should_create_scope() {
        if (bucketOperator.hasScope(scopeName)) {
            bucketOperator.dropScope(scopeName);
        }
        CreateScopeStatement createScopeStatement = new CreateScopeStatement(scopeName, bucketName);

        createScopeStatement.execute(database.getConnection());

        assertThat(createScopeStatement.getScopeName()).isEqualTo(scopeName);
    }

    @Test
    void Should_throw_exception_if_scope_exists() {
        CreateScopeStatement statement = new CreateScopeStatement(scopeName, bucketName);

        assertThatExceptionOfType(ScopeExistsException.class)
                .isThrownBy(() -> statement.execute(database.getConnection()))
                .withMessage("Scope [%s] already exists.", scopeName);
    }

}