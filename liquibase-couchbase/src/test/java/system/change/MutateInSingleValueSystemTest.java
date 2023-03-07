package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.operator.CollectionOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquiBaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INSERT_NO_PATH_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_REMOVE_DOCUMENT_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_REPLACE_DOCUMENT_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_UPSERT_REPLACE_REMOVE_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInSingleValueSystemTest extends LiquiBaseSystemTest {

    private static final CollectionOperator testCollectionOperator = new CollectionOperator(
            bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    @Test
    @SneakyThrows
    void Should_insert_update_remove_filed_in_document() {
        String id = "upsertReplaceDeleteId";
        JsonObject document = initDocumentForInsertUpdateRemove();
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquiBase(MUTATE_IN_UPSERT_REPLACE_REMOVE_TEST_XML);
        liquibase.update();

        JsonObject expected = expectedOfInsertUpdateRemove();
        assertThat(collection).extractingDocument(id).itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    private static JsonObject initDocumentForInsertUpdateRemove() {
        JsonObject document = JsonObject.create()
                .put("key", "value")
                .put("fieldToUpdate", "oldValue")
                .put("fieldToReplace", "oldValue")
                .put("fieldToDelete", "value");
        return document;
    }

    private static JsonObject expectedOfInsertUpdateRemove() {
        JsonObject expected = JsonObject.create()
                .put("key", "value")
                .put("fieldToUpdate", 42)
                .put("fieldToReplace", true)
                .put("newField", "newFieldValue");
        return expected;
    }

    @Test
    @SneakyThrows
    void Should_replace_document() {
        String id = "replaceDocument";
        testCollectionOperator.insertDoc(id, TEST_DOCUMENT);

        Liquibase liquibase = liquiBase(MUTATE_IN_REPLACE_DOCUMENT_TEST_XML);
        liquibase.update();

        JsonObject expected = JsonObject.create()
                .put("newDocumentField", "newDocumentValue");
        assertThat(collection).extractingDocument(id).itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    @Test
    @SneakyThrows
    void Should_remove_document() {
        String id = "removeDocument";
        testCollectionOperator.insertDoc(id, TEST_DOCUMENT);

        Liquibase liquibase = liquiBase(MUTATE_IN_REMOVE_DOCUMENT_TEST_XML);
        liquibase.update();

        assertThat(collection).hasNoDocument(id);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_insert_without_path() {
        String id = "insertNoPathError";
        testCollectionOperator.insertDoc(id, TEST_DOCUMENT);

        Liquibase liquibase = liquiBase(MUTATE_IN_INSERT_NO_PATH_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class).isThrownBy(liquibase::update);

        assertThat(collection).extractingDocument(id).itsContentEquals(TEST_DOCUMENT);
        testCollectionOperator.removeDoc(id);
    }

}
