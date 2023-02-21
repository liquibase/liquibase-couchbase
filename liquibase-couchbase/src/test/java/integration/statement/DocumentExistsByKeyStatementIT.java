package integration.statement;

import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.DocumentExistsByKeyStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentExistsByKeyStatementIT extends RandomizedScopeTestCase {
    private TestCollectionOperator collectionOperator;
    private Document document;
    private Keyspace keyspace;

    @BeforeEach
    public void setUp() {
        collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        keyspace = keyspace(bucketName, scopeName, collectionName);
        document = collectionOperator.generateTestDoc();
    }

    @Test
    void Should_return_true_when_document_exists() {
        collectionOperator.insertDoc(document);
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(keyspace, document.getId());

        assertThat(statement.isDocumentExists(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_document_doesnt_exists() {
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(keyspace, "notExistedKey");

        assertThat(statement.isDocumentExists(database.getConnection())).isFalse();
    }
}
