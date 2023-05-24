package integration.statement;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.google.common.collect.Sets;
import common.TransactionStatementTest;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.RemoveDocumentsQueryStatement;
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
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static common.constants.TestConstants.INDEX;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;

class RemoveDocumentsQueryStatementIT extends TransactionStatementTest {

    private static final String DOC_FIELD_NAME = "field";
    private static final String DOC_FIELD_VALUE = "val";
    private static final String testCollection = UUID.randomUUID().toString();
    private static final String testScope = UUID.randomUUID().toString();
    private static TestCollectionOperator collectionOperator;
    private static Collection collection;

    private Set<Id> ids;
    private Document doc1;
    private Document doc2;
    private Document doc3;
    private Keyspace keyspace = keyspace(bucketName, testScope, testCollection);

    @BeforeAll
    @SneakyThrows
    static void beforeClass() {
        bucketOperator.createScope(testScope);
        TimeUnit.SECONDS.sleep(2L);
        bucketOperator.createCollection(testCollection, testScope);
        TimeUnit.SECONDS.sleep(2L);
        collectionOperator = bucketOperator.getCollectionOperator(testCollection, testScope);
        collectionOperator.createPrimaryIndex(CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(INDEX));
        TimeUnit.SECONDS.sleep(2L);
        collection = bucketOperator.getCollection(testCollection, testScope);
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
        ids = Sets.newHashSet(new Id(doc3.getId()));
        collectionOperator.insertDocs(doc1, doc2, doc3);
        TimeUnit.SECONDS.sleep(3L);
    }

    @AfterEach
    void cleanUp() {
        if (collectionOperator.docExists(doc1.getId())) {
            collectionOperator.removeDoc(doc1);
        }
        if (collectionOperator.docExists(doc2.getId())) {
            collectionOperator.removeDoc(doc2);
        }
        if (collectionOperator.docExists(doc3.getId())) {
            collectionOperator.removeDoc(doc3);
        }
    }

    @Test
    void Should_remove_docks_by_where_condition() {
        RemoveDocumentsQueryStatement statement = new RemoveDocumentsQueryStatement(keyspace, ids, "field=\"val\"");

        doInTransaction(statement.asTransactionAction(clusterOperator));

        assertThat(collection).doesNotContainIds(doc1.getId(), doc2.getId(), doc3.getId());
    }

    @Test
    void Should_remove_docks_by_where_condition_like() {
        RemoveDocumentsQueryStatement statement = new RemoveDocumentsQueryStatement(keyspace, new HashSet<>(), "field LIKE \"%val%\"");

        doInTransaction(statement.asTransactionAction(clusterOperator));

        assertThat(collection).doesNotContainIds(doc1.getId(), doc2.getId(), doc3.getId());
    }

}
