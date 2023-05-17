package system.precondition;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static common.constants.ChangeLogSampleFilePaths.PRIMARY_INDEX_EXISTS_FAILED_PRECONDITION;
import static common.constants.ChangeLogSampleFilePaths.PRIMARY_INDEX_EXISTS_PRECONDITION;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class PrimaryIndexExistsPreconditionSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final String DOCUMENT_ID = "primaryIndexTestPreconditionId1";
    private static final String PRIMARY_INDEX_NAME = "createdPrimaryIndex";

    @Test
    @SneakyThrows
    void Should_insert_document_when_primary_index_exists() {
        CreatePrimaryQueryIndexOptions indexOptions = createPrimaryQueryIndexOptions().ignoreIfExists(
                false).indexName(PRIMARY_INDEX_NAME);
        collection.queryIndexes().createPrimaryIndex(indexOptions);
        Liquibase liquibase = liquibase(PRIMARY_INDEX_EXISTS_PRECONDITION);

        liquibase.update();

        assertThat(collection).containsId(DOCUMENT_ID);
        collection.remove(DOCUMENT_ID);
        collection.queryIndexes().dropIndex(PRIMARY_INDEX_NAME);
    }

    @Test
    @SneakyThrows
    void Should_not_insert_document_when_primary_index_not_exists() {
        Liquibase liquibase = liquibase(PRIMARY_INDEX_EXISTS_FAILED_PRECONDITION);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        assertThat(collection).doesNotContainId(DOCUMENT_ID);
    }
}
