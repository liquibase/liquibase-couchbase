package system;

import org.junit.jupiter.api.Test;

import liquibase.Liquibase;
import lombok.SneakyThrows;
import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseBucketAssert.assertThat;

public class CreateCollectionSystemTest extends LiquiBaseSystemTest {

    @Test
    @SneakyThrows
    void Collection_should_be_created_after_liquibase_execution() {
        Liquibase liquibase = liquiBase(CREATE_COLLECTION_TEST_XML);

        liquibase.update();

        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope("travels", TEST_SCOPE);
    }
}
