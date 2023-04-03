package integration.statement;

import com.couchbase.client.core.error.ScopeNotFoundException;
import com.couchbase.client.java.Bucket;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.DropScopeStatement;
import org.junit.jupiter.api.Test;

import static common.matchers.CouchbaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


class DropScopeStatementIT extends RandomizedScopeTestCase {

    private final Bucket bucket = clusterOperator.getBucketOperator(bucketName).getBucket();

    @Test
    void Should_drop_scope() {
        DropScopeStatement dropScopeStatement = new DropScopeStatement(scopeName, bucketName);

        dropScopeStatement.execute(clusterOperator);

        assertThat(bucket).hasNoScope(scopeName);
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