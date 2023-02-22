package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import common.operators.TestBucketOperator;
import common.operators.TestClusterOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.operator.CollectionOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquiBaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INCREMENT_DECREMENT_ERROR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.MUTATE_IN_INCREMENT_DECREMENT_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class MutateInIncrementDecrementSystemTest extends LiquiBaseSystemTest {

    private static final TestClusterOperator clusterOperator = new TestClusterOperator(cluster);
    private static final TestBucketOperator bucketOperator = clusterOperator.getBucketOperator(TEST_BUCKET);
    private static final CollectionOperator testCollectionOperator = new CollectionOperator(
        bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    @Test
    @SneakyThrows
    void Should_increment_and_decrement_existing_fields_and_increment_decrement_with_creating_new_fields() {
        String id = "incrementDecrement";
        JsonObject document = initDocument();
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquiBase(MUTATE_IN_INCREMENT_DECREMENT_TEST_XML);
        liquibase.update();

        JsonObject expected = expectedWithIncrementDecrementAndCreated();
        assertThat(collection).extractingDocument(id)
            .itsContentEquals(expected);

        testCollectionOperator.removeDoc(id);
    }

    private static JsonObject expectedWithIncrementDecrementAndCreated() {
        return JsonObject.create()
            .put("key", "value")
            .put("increment", 6)
            .put("decrement", 4)
            .put("newIncrement", 5)
            .put("newDecrement", -5);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_and_do_nothing_when_decrement_with_invalid_data_type() {
        String id = "incrementDecrementError";
        JsonObject document = initDocument();
        testCollectionOperator.insertDoc(id, document);

        Liquibase liquibase = liquiBase(MUTATE_IN_INCREMENT_DECREMENT_ERROR_TEST_XML);
        assertThatExceptionOfType(LiquibaseException.class).isThrownBy(liquibase::update);

        assertThat(collection).extractingDocument(id)
            .itsContentEquals(document);

        testCollectionOperator.removeDoc(id);
    }

    private static JsonObject initDocument() {
        return JsonObject.create()
            .put("key", "value")
            .put("increment", 5)
            .put("decrement", 5);
    }
}
