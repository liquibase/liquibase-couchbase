package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonObject;
import liquibase.Liquibase;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.List;
import java.util.stream.IntStream;

import static common.constants.ChangeLogSampleFilePaths.INSERT_FROM_FILE_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.types.Document.document;


public class InsertFromFileSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private final List<Document> testDocs = createDocs();
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
}
