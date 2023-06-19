package system.change;

import com.couchbase.client.java.Collection;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.Arrays;
import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENTS_FAILED_TRANSACTION_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENTS_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class InsertDocumentsSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    @BeforeAll
    static void setUp() {
        createPrimaryIndex();
    }

    @AfterAll
    static void tearDown() {
        dropPrimaryIndex();
    }

    private static void createPrimaryIndex() {
        bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE).createPrimaryIndex();
    }

    private static void dropPrimaryIndex() {
        bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE).dropCollectionPrimaryIndex();
    }

    @Test
    @SneakyThrows
    void Should_insert_documents_with_different_types() {
        List<Document> expectedDocuments = createExpectedDocumentsWithDifferentTypes();
        Liquibase liquibase = liquibase(INSERT_DOCUMENTS_TEST_XML);

        liquibase.update();

        assertThat(collection).contains(expectedDocuments);
        clusterOperator.removeAllDocuments(TEST_KEYSPACE);
    }

    private List<Document> createExpectedDocumentsWithDifferentTypes() {
        Document doc1 = document("id1", "{\"key\":\"value\"}", DataType.JSON);
        Document doc2 = document("id2", "StringData", DataType.STRING);
        Document doc3 = document("id3", "123", DataType.LONG);
        Document doc4 = document("id4", "[{\"key\":\"value1\"}, {\"key\":\"value2\"}]", DataType.JSON_ARRAY);
        Document doc5 = document("id5", "true", DataType.BOOLEAN);
        Document doc6 = document("id6", "123.12", DataType.DOUBLE);
        return Arrays.asList(doc1, doc2, doc3, doc4, doc5, doc6);
    }

    @Test
    @SneakyThrows
    void Should_not_insert_documents_when_transaction_is_failed() {
        Liquibase liquibase = liquibase(INSERT_DOCUMENTS_FAILED_TRANSACTION_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        assertThat(collection).doesNotContainIds("id1", "id3");
    }

}
