package integration.precondition;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.ConstantScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.exception.precondition.IndexNotExistsPreconditionException;
import liquibase.ext.couchbase.precondition.IndexExistsPrecondition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IndexExistsPreconditionIT extends ConstantScopeTestCase {

    private final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);

    @AfterEach
    void cleanUp() {
        if (collectionOperator.collectionIndexExists(INDEX)) {
            bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE).dropIndex(INDEX);
        }
    }

    @Test
    void Should_not_throw_exception_when_index_exists() {
        collectionOperator.createPrimaryIndex(CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(INDEX));
        IndexExistsPrecondition precondition = new IndexExistsPrecondition(TEST_BUCKET, INDEX, TEST_SCOPE, true);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_when_index_doesnt_exists() {
        IndexExistsPrecondition precondition = new IndexExistsPrecondition(TEST_BUCKET, INDEX, TEST_SCOPE, true);

        assertThatExceptionOfType(IndexNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage(format("Index %s(bucket name - %s, scope name - %s, primary - %s) does not exist", INDEX, TEST_BUCKET, TEST_SCOPE, true));
    }
}
