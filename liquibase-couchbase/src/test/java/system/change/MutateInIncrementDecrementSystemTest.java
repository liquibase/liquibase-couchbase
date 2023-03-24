package system.change;

import com.couchbase.client.java.json.JsonObject;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INCREMENT_DECREMENT_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INCREMENT_DECREMENT_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static common.operators.TestCollectionOperator.createTestDocContent;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInIncrementDecrementSystemTest extends LiquibaseSystemTest {

    private static final TestCollectionOperator testCollectionOperator =
            bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);

    @Test
    @SneakyThrows
    void Should_increment_and_decrement_existing_fields_and_increment_decrement_with_creating_new_fields() {
        Document doc = document("incrementDecrement", initDocument());
        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_INCREMENT_DECREMENT_TEST_XML);
        liquibase.update();

        Document expected = document(doc.getId(), expectedWithIncrementDecrementAndCreated());
        assertThat(testCollectionOperator.getCollection()).contains(expected);

        testCollectionOperator.removeDoc(doc);
    }


    @Test
    @SneakyThrows
    void Should_throw_error_and_do_nothing_when_decrement_with_invalid_data_type() {
        Document doc = document("incrementDecrementError", initDocument());

        testCollectionOperator.insertDoc(doc);

        Liquibase liquibase = liquibase(MUTATE_IN_INCREMENT_DECREMENT_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class).isThrownBy(liquibase::update);

        assertThat(testCollectionOperator.getCollection()).contains(doc);

        testCollectionOperator.removeDoc(doc);
    }

    private static JsonObject initDocument() {
        return createTestDocContent()
                .put("increment", 5)
                .put("decrement", 5);
    }


    private static JsonObject expectedWithIncrementDecrementAndCreated() {
        return createTestDocContent()
                .put("increment", 6)
                .put("decrement", 4)
                .put("newIncrement", 5)
                .put("newDecrement", -5);
    }
}
