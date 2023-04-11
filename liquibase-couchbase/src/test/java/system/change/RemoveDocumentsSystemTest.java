package system.change;

import com.couchbase.client.java.Collection;
import com.google.common.collect.Lists;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.REMOVE_NON_EXISTING_DOC_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_NON_EXISTING_DOC_MARK_AS_READ_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_ONE_DOCUMENT_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RemoveDocumentsSystemTest extends LiquibaseSystemTest {

    private final TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    private List<Id> ids;
    private Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    private String docId1 = "newTestDoc_ID1";
    private String docId2 = "newTestDoc_ID2";
    private Document doc1 = collectionOperator.generateTestDocById(docId1);
    private Document doc2 = collectionOperator.generateTestDocById(docId2);

    private void insertTestDocuments() {
        collectionOperator.insertDocs(doc1, doc2);
        ids = Lists.newArrayList(new Id(doc1.getId()));
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
    void Collections_should_be_deleted() {
        insertTestDocuments();
        Liquibase liquibase = liquibase(REMOVE_ONE_DOCUMENT_TEST_XML);
        liquibase.update();

        assertThat(collection).doesNotContainIds(ids);
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_document_should_be_mark_as_run_precondition() {
        Liquibase liquibase = liquibase(REMOVE_NON_EXISTING_DOC_MARK_AS_READ_TEST_XML);
        liquibase.update();
        assertDoesNotThrow(() -> liquibase.update());
    }

    @Test
    @SneakyThrows
    void Delete_non_existing_collection_should_throw_exception_precondition() {
        Liquibase liquibase = liquibase(REMOVE_NON_EXISTING_DOC_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update)
                .withMessageContaining("Key %s does not exist in bucket %s in scope %s and collection %s", docId1, TEST_BUCKET, TEST_SCOPE,
                        TEST_COLLECTION);
    }

}
