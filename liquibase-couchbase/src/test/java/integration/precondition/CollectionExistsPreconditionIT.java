package integration.precondition;

import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.exception.precondition.CollectionNotExistsPreconditionException;
import liquibase.ext.couchbase.precondition.CollectionExistsPrecondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CollectionExistsPreconditionIT extends RandomizedScopeTestCase {

    private static final CollectionExistsPrecondition precondition = new CollectionExistsPrecondition();

    @BeforeEach
    void setUpLocal() {
        precondition.setBucketName(bucketName);
        precondition.setScopeName(scopeName);
    }

    @Test
    void Should_not_throw_when_collection_exists() {
        precondition.setCollectionName(collectionName);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_exception_when_collection_doesnt_exists() {
        String notCreatedCollection = "notCreatedCollection";
        precondition.setCollectionName(notCreatedCollection);

        assertThatExceptionOfType(CollectionNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Collection %s does not exist in bucket %s in scope %s",
                        notCreatedCollection, bucketName, scopeName);
    }
}
