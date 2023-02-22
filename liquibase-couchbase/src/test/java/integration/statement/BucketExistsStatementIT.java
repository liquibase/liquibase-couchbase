package integration.statement;

import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.statement.BucketExistsStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;

class BucketExistsStatementIT extends ConstantScopeTestCase {

    @Test
    void Should_return_true_when_bucket_exists() {
        BucketExistsStatement statement = new BucketExistsStatement(TEST_BUCKET);

        assertThat(statement.isTrue(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_bucket_doesnt_exists() {
        BucketExistsStatement statement = new BucketExistsStatement("someNotCreatedBucket");

        assertThat(statement.isTrue(database.getConnection())).isFalse();
    }
}
