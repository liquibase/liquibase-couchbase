package integration.statement;

import org.junit.jupiter.api.Test;

import liquibase.ext.couchbase.statement.DocumentExistsByKeyStatement;
import common.BucketTestCase;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.constants.TestConstants.TEST_COLLECTION;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentExistsByKeyStatementIT extends BucketTestCase {

    @Test
    void Should_return_true_when_document_exists() {
        insertDocInTestCollection(TEST_ID, TEST_DOCUMENT);
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                TEST_ID
        );

        assertThat(statement.isCDocumentExists(database.getConnection())).isTrue();
        removeDocFromTestCollection(TEST_ID);
    }

    @Test
    void Should_return_false_when_document_doesnt_exists() {
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                "notExistedKey"
        );

        assertThat(statement.isCDocumentExists(database.getConnection())).isFalse();
    }
}
