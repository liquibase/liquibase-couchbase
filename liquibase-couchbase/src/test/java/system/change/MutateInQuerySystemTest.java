package system.change;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.concurrent.TimeUnit;

import static com.couchbase.client.java.manager.query.DropPrimaryQueryIndexOptions.dropPrimaryQueryIndexOptions;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_SQL_PLUS_PLUS_FILTER_REPLACE_DOCUMENT_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENTS_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENT_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION_3;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static common.operators.TestCollectionOperator.createOneFieldJson;
import static liquibase.ext.couchbase.types.Document.document;

public class MutateInQuerySystemTest extends LiquibaseSystemTest {

    private static TestCollectionOperator testCollectionOperator;

    private static final JsonObject expectedReplaceContent = createOneFieldJson("newDocumentField", "newDocumentValue");
    private static final String[] documentIds = {"replaceDocument1", "replaceDocument2", "replaceDocument3"};

    @BeforeEach
    void setUp() {
        for (String docId : documentIds) {
            if (testCollectionOperator.docExists(docId)) {
                testCollectionOperator.getCollection().remove(docId);
            }
        }
    }

    @AfterAll
    static void cleanIndex() {
        DropPrimaryQueryIndexOptions options = dropPrimaryQueryIndexOptions().ignoreIfNotExists(true);
        testCollectionOperator.dropPrimaryIndex(options);
    }

    @BeforeAll
    static void createPrimaryIndex() throws InterruptedException {
        bucketOperator.createCollection(TEST_COLLECTION_3, TEST_SCOPE);
        testCollectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION_3, TEST_SCOPE);
        TimeUnit.SECONDS.sleep(2L);
        testCollectionOperator.createPrimaryIndex();
        TimeUnit.SECONDS.sleep(2L);
    }

    @Test
    @SneakyThrows
    void Should_replace_document() {
        Document doc = document(documentIds[0], createOneFieldJson("aKey", "avalue"));
        Document expected = document(doc.getId(), expectedReplaceContent);
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENT_TEST_XML);
        liquibase.update();

        assertThat(testCollectionOperator.getCollection()).contains(expected);
    }

    @Test
    @SneakyThrows
    void Should_replace_documents() {
        Document doc1 = document(documentIds[0], createOneFieldJson("aKey1", "avalue"));
        Document doc2 = document(documentIds[1], createOneFieldJson("aKey1", "avalue"));
        Document doc3 = document(documentIds[2], createOneFieldJson("aKey1", "avalue5"));
        Document expected1 = document(doc1.getId(), expectedReplaceContent);
        Document expected2 = document(doc2.getId(), expectedReplaceContent);
        testCollectionOperator.insertDocs(doc1, doc2, doc3);

        Liquibase liquibase = liquibase(MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENTS_TEST_XML);
        liquibase.update();

        assertThat(testCollectionOperator.getCollection()).contains(expected1);
        assertThat(testCollectionOperator.getCollection()).contains(expected2);
        assertThat(testCollectionOperator.getCollection()).contains(doc3);
    }

    @Test
    @SneakyThrows
    void Should_replace_document_sqlPlusPlus_query() {
        Document doc = document(documentIds[0], createOneFieldJson("aKey", "avalue"));
        Document expected = document(doc.getId(), expectedReplaceContent);
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_SQL_PLUS_PLUS_FILTER_REPLACE_DOCUMENT_TEST_XML);
        liquibase.update();

        assertThat(testCollectionOperator.getCollection()).contains(expected);
    }

}
