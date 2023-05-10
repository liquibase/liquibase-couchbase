package system.precondition;

import com.couchbase.client.java.Collection;
import common.matchers.CouchbaseCollectionAssert;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.util.Arrays;

import static common.constants.ChangeLogSampleFilePaths.INDEX_EXISTS_FAILED_PRECONDITION;
import static common.constants.ChangeLogSampleFilePaths.INDEX_EXISTS_PRECONDITION;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class IndexExistsPreconditionSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final String DOCUMENT_ID = "indexTestPreconditionId1";

    @Test
    @SneakyThrows
    void Should_insert_document_when_index_exists() {
        collection.queryIndexes().createIndex("createdIndex", Arrays.asList("field1"));
        Liquibase liquibase = liquibase(INDEX_EXISTS_PRECONDITION);

        liquibase.update();

        CouchbaseCollectionAssert.assertThat(collection).containsId(DOCUMENT_ID);
        collection.remove(DOCUMENT_ID);
    }

    @Test
    @SneakyThrows
    void Should_not_insert_document_when_index_not_exists() {
        Liquibase liquibase = liquibase(INDEX_EXISTS_FAILED_PRECONDITION);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        CouchbaseCollectionAssert.assertThat(collection).doesNotContainId(DOCUMENT_ID);
    }
}
