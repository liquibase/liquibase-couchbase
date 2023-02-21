package integration.precondition;

import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.exception.precondition.DocumentNotExistsPreconditionException;
import liquibase.ext.couchbase.precondition.DocumentExistsByKeyPrecondition;
import liquibase.ext.couchbase.types.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class DocumentExistsByKeyPreconditionIT extends RandomizedScopeTestCase {

    private static final DocumentExistsByKeyPrecondition precondition = new DocumentExistsByKeyPrecondition();

    @BeforeEach
    void setUpLocal() {
        precondition.setBucketName(bucketName);
        precondition.setScopeName(scopeName);
        precondition.setCollectionName(collectionName);
    }

    @Test
    void Should_not_throw_when_document_exists() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        Document document = collectionOperator.generateTestDoc();
        collectionOperator.insertDoc(document);

        precondition.setKey(document.getId());

        assertDoesNotThrow(() -> precondition.check(database, null, null, null));
    }

    @Test
    void Should_throw_exception_when_document_doesnt_exists() {
        String notExistedKey = "notExistedKey";

        precondition.setKey(notExistedKey);

        assertThatExceptionOfType(DocumentNotExistsPreconditionException.class)
                .isThrownBy(() -> precondition.check(database, null, null, null))
                .withMessage("Key %s does not exist in bucket %s in scope %s and " +
                        "collection %s", notExistedKey, bucketName, scopeName, collectionName);
    }
}
