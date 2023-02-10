package integration.statement;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.DocumentExistsByKeyStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentExistsByKeyStatementIT extends BucketTestCase {

    @Test
    void Should_return_true_when_document_exists() {
        insertDocInTestCollection(TEST_ID, TEST_DOCUMENT);
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, TEST_ID);

        assertThat(statement.isDocumentExists(database.getConnection())).isTrue();
        removeDocFromTestCollection(TEST_ID);
    }

    @Test
    void Should_return_false_when_document_doesnt_exists() {
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, "notExistedKey");

        assertThat(statement.isDocumentExists(database.getConnection())).isFalse();
    }
}
