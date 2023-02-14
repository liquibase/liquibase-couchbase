package integration.precondition;

import common.BucketTestCase;
import liquibase.ext.couchbase.exception.precondition.CollectionsNotExistsPreconditionException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import liquibase.ext.couchbase.precondition.CollectionExistsPrecondition;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CollectionExistsPreconditionIT extends BucketTestCase {

    private static final CollectionExistsPrecondition precondition = new CollectionExistsPrecondition();

    @BeforeAll
    static void setUpLocal() {
        precondition.setBucketName(TEST_BUCKET);
        precondition.setScopeName(TEST_SCOPE);
    }

    @Test
    void Should_not_throws_when_collection_exists() {
        precondition.setCollectionName(TEST_COLLECTION);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_exception_when_collection_doesnt_exists() {
        String notCreatedCollection = "notCreatedCollection";
        precondition.setCollectionName(notCreatedCollection);

        assertThatExceptionOfType(CollectionsNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Collection %s does not exist in bucket %s in scope %s",
                        notCreatedCollection, TEST_BUCKET, TEST_SCOPE);
    }
}
