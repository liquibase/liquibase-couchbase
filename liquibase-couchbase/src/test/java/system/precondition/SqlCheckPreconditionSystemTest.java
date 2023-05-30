package system.precondition;

import com.couchbase.client.java.Collection;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.SQL_CHECK_FAILED_PRECONDITION;
import static common.constants.ChangeLogSampleFilePaths.SQL_CHECK_PRECONDITION;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SqlCheckPreconditionSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final String DOCUMENT_ID = "sqlCheckIndexTestPreconditionId1";

    @BeforeAll
    static void setUp() {
        collection.queryIndexes().createPrimaryIndex();
        clusterOperator.removeAllDocuments(TEST_KEYSPACE);
    }

    @AfterAll
    static void cleanUp() {
        collection.queryIndexes().dropPrimaryIndex();
    }

    @Test
    @SneakyThrows
    void Should_insert_document_when_sql_check_passed() {
        insertMockDocuments();
        Liquibase liquibase = liquibase(SQL_CHECK_PRECONDITION);

        liquibase.update();

        assertThat(collection).containsId(DOCUMENT_ID);
        collection.remove(DOCUMENT_ID);
        removeMockDocuments();
    }

    @Test
    @SneakyThrows
    void Should_not_insert_document_when_sql_check_not_passed() {
        insertMockDocuments();
        Liquibase liquibase = liquibase(SQL_CHECK_FAILED_PRECONDITION);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        assertThat(collection).doesNotContainId(DOCUMENT_ID);
        removeMockDocuments();
    }

    private void insertMockDocuments() {
        collection.insert("id1", EMPTY);
        collection.insert("id2", EMPTY);
        collection.insert("id3", EMPTY);
    }

    private void removeMockDocuments() {
        collection.remove("id1");
        collection.remove("id2");
        collection.remove("id3");
    }
}
