package liquibase.integration.precondition;

import com.couchbase.client.java.Bucket;
import com.wdt.couchbase.exception.DocumentNotExistsPreconditionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.ext.precondition.DocumentExistsByKeyPrecondition;
import liquibase.integration.BucketTestCase;
import static liquibase.common.constants.TestConstants.TEST_BUCKET;
import static liquibase.common.constants.TestConstants.TEST_COLLECTION;
import static liquibase.common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DocumentExistsByKeyPreconditionIT extends BucketTestCase {

    private static Bucket bucket;
    private static final DocumentExistsByKeyPrecondition precondition = new DocumentExistsByKeyPrecondition();

    @BeforeEach
    void setUpLocal() {
        precondition.setBucketName(TEST_BUCKET);
        precondition.setScopeName(TEST_SCOPE);
        precondition.setCollectionName(TEST_COLLECTION);
        bucket = cluster.bucket(TEST_BUCKET);
    }

    @Test
    void Should_not_throws_when_document_exists() {
        String key = "key";
        bucket.scope(TEST_SCOPE).collection(TEST_COLLECTION).insert(key, "testObject");

        precondition.setKey(key);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));

        bucket.scope(TEST_SCOPE).collection(TEST_COLLECTION).remove(key);
    }

    @Test
    void Should_throw_exception_when_document_doesnt_exists() {
        String notExistedKey = "notExistedKey";

        precondition.setKey(notExistedKey);

        assertThatExceptionOfType(DocumentNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Key %s does not exist in bucket %s in scope %s and " +
                        "collection %s", notExistedKey, TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
    }
}
