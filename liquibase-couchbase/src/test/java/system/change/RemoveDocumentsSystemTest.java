package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.google.common.collect.Sets;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import static common.constants.ChangeLogSampleFilePaths.REMOVE_DOCUMENTS_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_NON_EXISTING_DOC_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_NON_EXISTING_DOC_MARK_AS_READ_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_ONE_DOCUMENT_TEST_XML;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RemoveDocumentsSystemTest extends LiquibaseSystemTest {

    private static final String DOC_FIELD_NAME = "field1";
    private static final String DOC_FIELD_VALUE = "val1";

    private static final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    private Set<Id> ids;
    private Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    private String docId1 = "newTestDoc_ID1";
    private String docId2 = "newTestDoc_ID2";
    private Document doc1 = collectionOperator.generateTestDocById(docId1);
    private Document doc2 = collectionOperator.generateTestDocById(docId2);


    @BeforeAll
    @SneakyThrows
    static void beforeAll() {
        collectionOperator.createPrimaryIndex(CreatePrimaryQueryIndexOptions
                .createPrimaryQueryIndexOptions()
                .indexName(INDEX));
        TimeUnit.SECONDS.sleep(5L);
    }

    @AfterAll
    static void afterAll() {
        if (collectionOperator.collectionIndexExists(INDEX)) {
            collectionOperator.dropIndex(INDEX);
        }
    }

    private void insertTestDocuments() {
        collectionOperator.insertDocs(doc1, doc2);
        ids = Sets.newHashSet(new Id(doc1.getId()));
    }

    private void insertDocsUsingSpecificFields() {
        Document document1 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, DOC_FIELD_VALUE));
        Document document2 = collectionOperator.generateTestDocByBody(JsonObject.create().put(DOC_FIELD_NAME, DOC_FIELD_VALUE));
        collectionOperator.insertDocs(document1, document2);
        ids = Sets.newHashSet(new Id(document1.getId()), new Id(document2.getId()));
    }

    @AfterEach
    void cleanUpd() {
        if (collectionOperator.docExists(docId1)) {
            collectionOperator.removeDoc(doc1);
        }
        if (collectionOperator.docExists(docId2)) {
            collectionOperator.removeDoc(doc2);
        }
    }

    @Test
    @SneakyThrows
    void Document_should_be_deleted() {
        insertTestDocuments();
        Liquibase liquibase = liquibase(REMOVE_ONE_DOCUMENT_TEST_XML);

        liquibase.update();

        assertThat(collection).doesNotContainIds(ids);
    }

    @Test
    @SneakyThrows
    void Documents_should_be_deleted() {
        insertDocsUsingSpecificFields();
        Liquibase liquibase = liquibase(REMOVE_DOCUMENTS_TEST_XML);

        liquibase.update();

        assertThat(collection).doesNotContainIds(ids);
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_document_should_be_mark_as_run_precondition() {
        Liquibase liquibase = liquibase(REMOVE_NON_EXISTING_DOC_MARK_AS_READ_TEST_XML);
        assertDoesNotThrow(() -> liquibase.update());
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_document_should_throw_exception_precondition() {
        Liquibase liquibase = liquibase(REMOVE_NON_EXISTING_DOC_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update)
                .withMessageContaining("Key %s does not exist in bucket %s in scope %s and collection %s", docId1, TEST_BUCKET, TEST_SCOPE,
                        TEST_COLLECTION);
    }

}
