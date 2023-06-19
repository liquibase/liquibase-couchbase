package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import liquibase.Liquibase;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.CollectionAssert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static common.constants.ChangeLogSampleFilePaths.INSERT_EXPRESSION_KEY_GENERATOR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_FROM_FILE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_INCREMENT_KEY_GENERATOR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_UID_KEY_GENERATOR_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.types.Document.document;
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class InsertDocumentsFromFileSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final int VALID_DOCS_COUNT = 2;
    private static final String EXTRA_FIELD = "extraField";
    private final List<Document> testDocs = createDocs();

    private static final String QUERY_ALL_DOC_ID = "SELECT META().id " +
            "FROM `" + TEST_BUCKET + "`.`" + TEST_SCOPE + "`.`" + TEST_COLLECTION + "`";

    @BeforeAll
    static void setUp() {
        createPrimaryIndex();
    }

    @AfterAll
    static void tearDown() {
        dropPrimaryIndex();
    }

    @AfterEach
    void clean() {
        clusterOperator.removeAllDocuments(TEST_KEYSPACE);
    }

    @Test
    @SneakyThrows
    void Should_insert_documents() {
        Liquibase liquibase = liquibase(INSERT_FROM_FILE_TEST_XML);

        liquibase.update();

        assertThat(collection).contains(testDocs);
    }

    private static List<Document> createDocs() {
        return IntStream.range(1, 5)
                .mapToObj(i -> document("id" + i, createJson(i)))
                .collect(toList());
    }

    private static JsonObject createJson(int i) {
        return JsonObject.create().put("id", "id" + i).put("value", "value" + i);
    }

    @Test
    @SneakyThrows
    void Should_generate_uid_key() {
        Liquibase liquibase = liquibase(INSERT_UID_KEY_GENERATOR_TEST_XML);

        Assertions.assertThatNoException().isThrownBy(liquibase::update);

        List<JsonObject> validDocs = selectValidDocs(InsertDocumentsFromFileSystemTest::isDocWithCorrectUid);

        CollectionAssert.assertThatCollection(validDocs).hasSize(VALID_DOCS_COUNT);
    }

    private static List<JsonObject> selectValidDocs(Predicate<JsonObject> condition) {
        QueryOptions options = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        return cluster.query(QUERY_ALL_DOC_ID, options).rowsAsObject()
                .stream().filter(condition::test).collect(toList());
    }

    private static void createPrimaryIndex() {
        clusterOperator.getBucketOperator(TEST_BUCKET)
                .getCollectionOperator(TEST_COLLECTION, TEST_SCOPE).createPrimaryIndex();
    }

    private static void dropPrimaryIndex() {
        bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE).dropCollectionPrimaryIndex();
    }

    private static boolean isDocWithCorrectUid(JsonObject doc) {
        try {
            if (isFalse(doc.containsKey("id"))) {
                return false;
            }
            UUID.fromString(getDocId(doc));
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    private static boolean isDocWithNumberId(JsonObject doc) {
        try {
            if (isFalse(doc.containsKey("id"))) {
                return false;
            }
            Long.parseLong(getDocId(doc));
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Test
    @SneakyThrows
    void Should_generate_incremental_key() {
        Liquibase liquibase = liquibase(INSERT_INCREMENT_KEY_GENERATOR_TEST_XML);

        Assertions.assertThatNoException().isThrownBy(liquibase::update);
        List<JsonObject> validDocs = selectValidDocs(InsertDocumentsFromFileSystemTest::isDocWithNumberId);

        validDocs.sort(Comparator.comparing(doc -> new Long(getDocId(doc))));
        CollectionAssert.assertThatCollection(validDocs).hasSize(VALID_DOCS_COUNT);
        IntStream.range(0, 2).forEach(i -> assertEquals(i, Long.parseLong(getDocId(validDocs.get(i)))));
    }

    private static String getDocId(JsonObject doc) {
        return (String) doc.get("id");
    }

    @Test
    @SneakyThrows
    void Should_generate_expression_key() {
        Liquibase liquibase = liquibase(INSERT_EXPRESSION_KEY_GENERATOR_TEST_XML);

        Assertions.assertThatNoException().isThrownBy(liquibase::update);
        assertThat(collection).extractingDocument("testKey::id1::0").isJson().hasField(EXTRA_FIELD);
        assertThat(collection).extractingDocument("testKey::id2::1").isJson().hasField(EXTRA_FIELD);
    }
}
