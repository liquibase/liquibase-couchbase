package system.change;

import com.couchbase.client.java.json.JsonObject;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_CREATE_DOCUMENT_AND_INSERT_FIELD_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INSERT_NO_PATH_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_REMOVE_DOCUMENT_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_REPLACE_DOCUMENT_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_UPSERT_REPLACE_REMOVE_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static common.operators.TestCollectionOperator.createOneFieldJson;
import static common.operators.TestCollectionOperator.createTestDocContent;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInSingleValueSystemTest extends LiquibaseSystemTest {

    private static final TestCollectionOperator testCollectionOperator =
            bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    private static final JsonObject expectedReplaceContent = createOneFieldJson("newDocumentField", "newDocumentValue");
    private static final JsonObject expectedInsertContent = createOneFieldJson("field", "HelloWorld");

    @Test
    @SneakyThrows
    void Should_insert_update_remove_filed_in_document() {
        Document doc = document("upsertReplaceDeleteId", initDocumentForInsertUpdateRemove());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_UPSERT_REPLACE_REMOVE_TEST_XML);
        liquibase.update();

        Document expected = document(doc.getId(), expectedOfInsertUpdateRemove());
        assertThat(testCollectionOperator.getCollection()).contains(expected);

        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_replace_document() {
        Document doc = document("replaceDocument", createTestDocContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_REPLACE_DOCUMENT_TEST_XML);
        liquibase.update();

        Document expected = document(doc.getId(), expectedReplaceContent);
        assertThat(testCollectionOperator.getCollection()).contains(expected);

        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_remove_document() {
        Document doc = document("removeDocument", createTestDocContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_REMOVE_DOCUMENT_TEST_XML);
        liquibase.update();

        assertThat(testCollectionOperator.getCollection()).doesNotContain(doc);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_insert_without_path() {
        Document doc = document("insertNoPathError", createTestDocContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_INSERT_NO_PATH_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class).isThrownBy(liquibase::update);

        assertThat(testCollectionOperator.getCollection()).contains(doc);
        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_create_document_and_insert_field() {
        Document doc = document("newDocumentMutateInId", createTestDocContent());

        Liquibase liquibase = liquibase(MUTATE_IN_CREATE_DOCUMENT_AND_INSERT_FIELD_TEST_XML);
        liquibase.update();

        Document expected = document(doc.getId(), expectedInsertContent);
        assertThat(testCollectionOperator.getCollection()).contains(expected);

        testCollectionOperator.removeDoc(doc);
    }


    private static JsonObject initDocumentForInsertUpdateRemove() {
        return createTestDocContent()
                .put("fieldToUpdate", "oldValue")
                .put("fieldToReplace", "oldValue")
                .put("fieldToDelete", "value");
    }

    private static JsonObject expectedOfInsertUpdateRemove() {
        return createTestDocContent()
                .put("fieldToUpdate", 42)
                .put("fieldToReplace", true)
                .put("newField", "newFieldValue");
    }

}
