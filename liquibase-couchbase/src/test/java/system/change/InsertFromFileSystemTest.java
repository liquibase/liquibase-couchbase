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
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static common.constants.ChangeLogSampleFilePaths.INSERT_FROM_FILE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.KEY_GENERATORS_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.types.Document.document;
import static org.apache.commons.lang3.BooleanUtils.isFalse;


public class InsertFromFileSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    public static final int VALID_DOCS_COUNT = 2;
    private final List<Document> testDocs = createDocs();

    private static final String QUERY_ALL_DOC_ID = "SELECT META().id " +
            "FROM `" + TEST_BUCKET + "`.`" + TEST_SCOPE + "`.`" + collection.name() + "`";

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
        Liquibase liquibase = liquibase(KEY_GENERATORS_TEST_XML);

        Assertions.assertThatNoException().isThrownBy(liquibase::update);
        createPrimaryIndex();
        QueryOptions options = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        List<JsonObject> validDocs = cluster.query(QUERY_ALL_DOC_ID, options).rowsAsObject()
                .stream().filter(InsertFromFileSystemTest::isDocWithCorrectUid).collect(toList());
        dropPrimaryIndex();
        CollectionAssert.assertThatCollection(validDocs).hasSize(VALID_DOCS_COUNT);
    }

    private static void createPrimaryIndex() {
        clusterOperator.createPrimaryIndex(TEST_KEYSPACE);
    }

    private static void dropPrimaryIndex() {
        clusterOperator.dropPrimaryIndex(TEST_KEYSPACE);
    }

    private static boolean isDocWithCorrectUid(JsonObject doc) {
        try {
            if (isFalse(doc.containsKey("id"))) {
                return false;
            }
            UUID.fromString((String) doc.get("id"));
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
