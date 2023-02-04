package liquibase.integration.precondition;

import com.wdt.couchbase.exception.BucketNotExistsPreconditionException;

import org.junit.jupiter.api.Test;

import liquibase.ext.precondition.BucketExistsPrecondition;
import liquibase.integration.BucketTestCase;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BucketExistsPreconditionIT extends BucketTestCase {

    @Test
    void Should_not_throw_exception_when_bucket_exists() {
        BucketExistsPrecondition precondition = new BucketExistsPrecondition();
        precondition.setBucketName(TEST_BUCKET);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_when_bucket_doesnt_exists() {
        BucketExistsPrecondition precondition = new BucketExistsPrecondition();
        precondition.setBucketName("someNotCreatedBucket");

        assertThatExceptionOfType(BucketNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Bucket someNotCreatedBucket does not exist");
    }
}
