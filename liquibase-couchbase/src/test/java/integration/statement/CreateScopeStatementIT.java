package integration.statement;

import com.couchbase.client.core.error.ScopeExistsException;
import com.couchbase.client.java.Bucket;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.CreateScopeStatement;
import org.junit.jupiter.api.Test;

import static common.matchers.CouchbaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class CreateScopeStatementIT extends RandomizedScopeTestCase {

    private final String createScopeName = CreateScopeStatementIT.class.getSimpleName();
    private final Bucket bucket = bucketOperator.getBucket();

    @Test
    void Should_create_scope() {
        CreateScopeStatement createScopeStatement = new CreateScopeStatement(createScopeName, bucketName);

        createScopeStatement.execute(clusterOperator);

        assertThat(bucket).hasScope(createScopeName);
    }

    @Test
    void Should_throw_exception_if_scope_exists() {
        CreateScopeStatement statement = new CreateScopeStatement(scopeName, bucketName);

        assertThatExceptionOfType(ScopeExistsException.class)
                .isThrownBy(() -> statement.execute(clusterOperator))
                .withMessage("Scope [%s] already exists.", scopeName);
    }

}