package system.change;

import com.couchbase.client.java.Collection;
import liquibase.Liquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_PRIMARY_QUERY_INDEX_TEST_XML;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

public class CreatePrimaryIndexSystemTest extends LiquibaseSystemTest {

    private static final String COLLECTION_NAME = "travel-sample";
    private static final String QUERY_INDEX_NAME = "testTravelPrimaryIndex";
    private static final Collection collection = bucketOperator.createOrGetCollection(COLLECTION_NAME, TEST_SCOPE);

    @AfterAll
    public static void cleanAfterAll() {
        if (bucketOperator.hasCollectionInScope(COLLECTION_NAME, TEST_SCOPE)) {
            bucketOperator.dropCollection(COLLECTION_NAME, TEST_SCOPE);
        }
    }

    @Test
    @SneakyThrows
    void Query_index_should_be_created_and_rolled_back() {
        Liquibase liquibase = liquibase(CREATE_PRIMARY_QUERY_INDEX_TEST_XML);

        liquibase.update();
        assertThat(collection).hasIndex(QUERY_INDEX_NAME);

        liquibase.rollback(1, null);
        assertThat(collection).hasNoIndex(QUERY_INDEX_NAME);
    }
}
