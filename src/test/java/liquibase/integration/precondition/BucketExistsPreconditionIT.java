package liquibase.integration.precondition;

import com.wdt.couchbase.exception.BucketNotExistsPreconditionException;
import liquibase.common.connection.*;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.precondition.BucketExistsPrecondition;
import liquibase.integration.CouchbaseContainerizedTest;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BucketExistsPreconditionIT extends CouchbaseContainerizedTest {

    private static CouchbaseLiquibaseDatabase database;

    @BeforeAll
    static void setUpLocal() {
        database = new TestCouchbaseDatabase(container);
    }

    @AfterAll
    @SneakyThrows
    static void tearDownLocal() {
        database.close();
    }

    @Test
    @DisplayName("Should not throw exception when bucket exists")
    void Should_not_throw_exception_when_bucket_exists() {
        BucketExistsPrecondition precondition = new BucketExistsPrecondition();
        precondition.setBucketName(TEST_BUCKET);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    @DisplayName("Should throw exception when bucket doesn't exist")
    void Should_throw_when_bucket_doesnt_exists() {
        BucketExistsPrecondition precondition = new BucketExistsPrecondition();
        precondition.setBucketName("someNotCreatedBucket");

        assertThatExceptionOfType(BucketNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Bucket someNotCreatedBucket does not exist");
    }
}
