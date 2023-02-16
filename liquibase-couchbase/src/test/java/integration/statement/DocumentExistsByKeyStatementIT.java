package integration.statement;

import common.BucketTestCase;
import common.operators.TestBucketOperator;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.DocumentExistsByKeyStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentExistsByKeyStatementIT extends BucketTestCase {
    //TODO use random doc IDs in other tests
    private String testDocId;
    private TestCollectionOperator testCollectionOperator;

    @BeforeEach
    public void setUp() {
        TestBucketOperator bucketOperator = new TestBucketOperator(getBucket());
        testCollectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
        testDocId = testCollectionOperator.insertTestDoc(TEST_DOCUMENT);
    }

    @Test
    void Should_return_true_when_document_exists() {
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, testDocId);

        assertThat(statement.isDocumentExists(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_document_doesnt_exists() {
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(TEST_KEYSPACE, "notExistedKey");

        assertThat(statement.isDocumentExists(database.getConnection())).isFalse();
    }
}
