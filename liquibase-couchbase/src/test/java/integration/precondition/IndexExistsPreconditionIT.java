package integration.precondition;

import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.exception.precondition.IndexNotExistsPreconditionException;
import liquibase.ext.couchbase.precondition.IndexExistsPrecondition;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class IndexExistsPreconditionIT extends ConstantScopeTestCase {

    @AfterEach
    void cleanUp() {
        if (clusterOperator.indexExists(INDEX, TEST_BUCKET)) {
            clusterOperator.dropIndex(INDEX, TEST_BUCKET);
        }
    }

    @Test
    void Should_not_throw_exception_when_bucket_exists() {
        clusterOperator.createPrimaryIndex(TEST_BUCKET, CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(INDEX)
                .ignoreIfExists(true));
        IndexExistsPrecondition precondition = new IndexExistsPrecondition(TEST_BUCKET, INDEX, true);

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_when_bucket_doesnt_exists() {
        IndexExistsPrecondition precondition = new IndexExistsPrecondition(TEST_BUCKET, INDEX, true);

        assertThatExceptionOfType(IndexNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage(format("Index %s(bucket name - %s, primary - %s) does not exist", INDEX, TEST_BUCKET, true));
    }
}
