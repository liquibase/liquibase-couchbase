package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.operator.CollectionOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.Arrays;
import java.util.Collections;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_APPEND_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_CREATE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_PREPEND_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_UNIQUE_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_ARRAY_UNIQUE_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInArraySystemTest extends LiquibaseSystemTest {

    private static final CollectionOperator testCollectionOperator = new CollectionOperator(
            bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    @Test
    @SneakyThrows
    void Should_insert_array_to_existing_document() {
        String id = "mutateInArrayCreateId";
        testCollectionOperator.insertDoc(id, TEST_DOCUMENT);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_CREATE_TEST_XML);
        liquibase.update();

        JsonObject expected = JsonObject.create()
                .put("arr", Collections.singletonList("firstValue"))
                .put("key", "value");
        assertThat(collection).extractingDocument(id).hasField("arr").itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    @Test
    @SneakyThrows
    void Should_add_new_value_to_end_of_array() {
        String id = "mutateInArrayAppendId";
        JsonObject document = JsonObject.create().put("key", "value").put("arr", Collections.singletonList("oldValue"));
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_APPEND_TEST_XML);
        liquibase.update();

        JsonObject expected = JsonObject.create()
                .put("arr", Arrays.asList("oldValue", "appendValue"))
                .put("key", "value");
        assertThat(collection).extractingDocument(id).hasField("arr").itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    @Test
    @SneakyThrows
    void Should_add_new_value_to_begin_of_array() {
        String id = "mutateInArrayPrependId";
        JsonObject document = JsonObject.create().put("key", "value").put("arr", Collections.singletonList("oldValue"));
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_PREPEND_TEST_XML);
        liquibase.update();

        JsonObject expected = JsonObject.create()
                .put("arr", Arrays.asList("prependValue", "oldValue"))
                .put("key", "value");
        assertThat(collection).extractingDocument(id).hasField("arr").itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    @Test
    @SneakyThrows
    void Should_insert_unique_value_to_array_when_no_exists() {
        String id = "mutateInArrayUniqueId";
        JsonObject document = JsonObject.create().put("key", "value").put("arr", Collections.singletonList("oldValue"));
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_UNIQUE_TEST_XML);
        liquibase.update();

        JsonObject expected = JsonObject.create()
                .put("arr", Arrays.asList("oldValue", "newValue"))
                .put("key", "value");
        assertThat(collection).extractingDocument(id).hasField("arr").itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_insert_new_value_to_array_which_exists() {
        String id = "mutateInArrayUniqueErrorId";
        JsonObject document = JsonObject.create().put("key", "value").put("arr", Collections.singletonList("oldValue"));
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquibase(MUTATE_IN_ARRAY_UNIQUE_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class).isThrownBy(liquibase::update);

        JsonObject expected = JsonObject.create()
                .put("arr", Collections.singletonList("oldValue"))
                .put("key", "value");
        assertThat(collection).extractingDocument(id).hasField("arr").itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

}
