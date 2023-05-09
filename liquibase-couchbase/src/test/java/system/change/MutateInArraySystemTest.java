package system.change;

import com.couchbase.client.java.json.JsonObject;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_APPEND_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_CREATE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_PREPEND_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_UNIQUE_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_UNIQUE_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static common.operators.TestCollectionOperator.createTestDocContent;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInArraySystemTest extends LiquibaseSystemTest {
    private static final String ARR = "arr";

    private static final TestCollectionOperator testCollectionOperator =
            bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);

    @Test
    @SneakyThrows
    void Should_insert_array_to_existing_document() {
        Document doc = document("mutateInArrayCreateId", createTestDocContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_CREATE_TEST_XML);
        liquibase.update();
        Document expected = document(doc.getId(), expectedAddToExistingDoc());
        assertThat(testCollectionOperator.getCollection()).containsDocument(expected).jsonHasField(ARR);

        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_add_new_value_to_end_of_array() {
        Document doc = document("mutateInArrayAppendId", createTestContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_APPEND_TEST_XML);
        liquibase.update();

        Document expected = document(doc.getId(), expectedAddValueToEnd());
        assertThat(testCollectionOperator.getCollection()).containsDocument(expected).jsonHasField(ARR);

        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_add_new_value_to_begin_of_array() {
        Document doc = document("mutateInArrayPrependId", createTestContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_PREPEND_TEST_XML);
        liquibase.update();
        Document expected = document(doc.getId(), expectedAddToBeginOfArray());
        assertThat(testCollectionOperator.getCollection()).containsDocument(expected).jsonHasField(ARR);

        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_insert_unique_value_to_array_when_no_exists() {
        Document doc = document("mutateInArrayUniqueId", createTestContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_UNIQUE_TEST_XML);
        liquibase.update();
        Document expected = document(doc.getId(), expectedNoExistContent());

        assertThat(testCollectionOperator.getCollection()).containsDocument(expected).jsonHasField(ARR);

        testCollectionOperator.removeDoc(doc);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_insert_new_value_to_array_which_exists() {
        Document doc = document("mutateInArrayUniqueErrorId", createTestContent());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_UNIQUE_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class).isThrownBy(liquibase::update);

        assertThat(testCollectionOperator.getCollection()).contains(doc);

        testCollectionOperator.removeDoc(doc);
    }

    private static JsonObject createTestContent() {
        return createTestDocContent()
                .put("arr", Collections.singletonList("oldValue"));
    }

    private static JsonObject expectedAddValueToEnd() {
        return arrayAndTestData(Arrays.asList("oldValue", "appendValue"));
    }

    private static JsonObject expectedAddToExistingDoc() {
        return arrayAndTestData(Collections.singletonList("firstValue"));
    }

    private static JsonObject expectedAddToBeginOfArray() {
        return arrayAndTestData(Arrays.asList("prependValue", "oldValue"));
    }

    private static JsonObject expectedNoExistContent() {
        return arrayAndTestData(Arrays.asList("oldValue", "newValue"));
    }

    private static JsonObject arrayAndTestData(List<String> content) {
        return JsonObject.create()
                .put(ARR, content)
                .put("key", "value");
    }

}
