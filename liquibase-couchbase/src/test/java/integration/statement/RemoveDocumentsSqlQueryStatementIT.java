package integration.statement;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.RemoveDocumentsSqlQueryStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Keyspace;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static common.constants.TestConstants.INDEX;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static java.lang.String.format;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

class RemoveDocumentsSqlQueryStatementIT extends TransactionStatementTest {

    private static final String DOC_FIELD_NAME = "field";
    private static final String DOC_FIELD_VALUE = "val";
    private static final String testScope = bucketOperator.createTestScope();
    private static final String testCollection = bucketOperator.createTestCollection(testScope);
    private static TestCollectionOperator collectionOperator;

    private Set<Id> ids;
    private Document doc1;
    private Document doc2;
    private Document doc3;
    private final Keyspace keyspace = keyspace(bucketName, testScope, testCollection);

    @BeforeAll
    @SneakyThrows
    static void beforeClass() {
        TimeUnit.SECONDS.sleep(1L);
        collectionOperator = bucketOperator.getCollectionOperator(testCollection, testScope);
        collectionOperator.createPrimaryIndex(CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(INDEX));
        TimeUnit.SECONDS.sleep(2L);
        //collection = bucketOperator.getCollection(testCollection, testScope);
    }

    @AfterAll
    static void afterAll() {
        if (collectionOperator.collectionIndexExists(INDEX)) {
            collectionOperator.dropIndex(INDEX);
        }
        bucketOperator.dropCollection(testCollection, testScope);
        bucketOperator.dropScope(testScope);
    }

    @BeforeEach
    @SneakyThrows
    void setUp() {
        doc1 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, DOC_FIELD_VALUE));
        doc2 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, DOC_FIELD_VALUE));
        doc3 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, "val5"));
        ids = new HashSet<>();
        ids.add(new Id(doc3.getId()));
        collectionOperator.insertDocs(doc1, doc2, doc3);
        TimeUnit.SECONDS.sleep(3L);
    }

    @AfterEach
    void cleanUp() {
        collectionOperator.removeDocsIfExist(doc1, doc2, doc3);
    }

    @Test
    void Should_remove_docs_by_where_condition() {
        RemoveDocumentsSqlQueryStatement statement = new RemoveDocumentsSqlQueryStatement(keyspace, ids,
                format("SELECT meta().id FROM %s where field=\"val\"", keyspace.getFullPath()));

        doInTransaction(statement.asTransactionAction(clusterOperator));

        assertThat(collectionOperator.getCollection()).doesNotContainIds(doc1.getId(), doc2.getId(), doc3.getId());
    }

    @Test
    void Should_remove_docs_by_where_condition_like() {
        RemoveDocumentsSqlQueryStatement statement = new RemoveDocumentsSqlQueryStatement(keyspace, new HashSet<>(),
                format("SELECT meta().id FROM %s where field LIKE ", keyspace.getFullPath()) + "\"%val%\"");

        doInTransaction(statement.asTransactionAction(clusterOperator));

        assertThat(collectionOperator.getCollection()).doesNotContainIds(doc1.getId(), doc2.getId(), doc3.getId());
    }

}
