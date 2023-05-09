package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import common.operators.TestCollectionOperator;
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

import static common.constants.ChangeLogSampleFilePaths.UPSERT_DOCUMENTS_FAILED_TRANSACTION_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.UPSERT_DOCUMENTS_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class UpsertDocumentsSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final Scope scope = cluster.bucket(collection.bucketName()).scope(collection.scopeName());
    private static final TestCollectionOperator collectionOperator = new TestCollectionOperator(collection);

    @BeforeAll
    static void setUp() {
        createPrimaryIndex();
    }

    @AfterAll
    static void tearDown() {
        dropPrimaryIndex();
    }

    private static void createPrimaryIndex() {
        Collection col = clusterOperator.getBucketOperator(TEST_BUCKET).
                getCollection(TEST_COLLECTION, TEST_SCOPE);
        clusterOperator.getCollectionOperator(col).createPrimaryIndex();
    }

    private static void dropPrimaryIndex() {
        clusterOperator.getCollectionOperator(collection).dropCollectionPrimaryIndex();
    }

    @Test
    @SneakyThrows
    void Should_upsert_documents_with_different_types() {
        prepareDocuments();
        List<Document> expectedDocuments = createExpectedDocumentsWithDifferentTypes();
        Liquibase liquibase = liquibase(UPSERT_DOCUMENTS_TEST_XML);

        liquibase.update();

        assertThat(collection).contains(expectedDocuments);
        collectionOperator.removeAllDocuments(scope);
    }

    private void prepareDocuments() {
        Document doc1 = document("id1", "SomeData1", DataType.STRING);
        Document doc2 = document("id2", "SomeData2", DataType.STRING);
        Document doc3 = document("id3", "111", DataType.LONG);
        collectionOperator.insertDocs(doc1, doc2, doc3);
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
    void Should_not_upsert_documents_when_transaction_is_failed() {
        Liquibase liquibase = liquibase(UPSERT_DOCUMENTS_FAILED_TRANSACTION_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        assertThat(collection).doesNotContainIds("id1", "id3");
    }

}
