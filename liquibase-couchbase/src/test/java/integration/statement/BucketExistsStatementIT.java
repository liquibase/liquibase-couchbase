package integration.statement;

import org.junit.jupiter.api.Test;

import liquibase.ext.couchbase.statement.BucketExistsStatement;
import common.BucketTestCase;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;

class BucketExistsStatementIT extends BucketTestCase {

    @Test
    void Should_return_true_when_bucket_exists() {
        BucketExistsStatement statement = new BucketExistsStatement(TEST_BUCKET);

        assertThat(statement.isBucketExists(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_bucket_doesnt_exists() {
        BucketExistsStatement statement = new BucketExistsStatement("someNotCreatedBucket");

        assertThat(statement.isBucketExists(database.getConnection())).isFalse();
    }
}
