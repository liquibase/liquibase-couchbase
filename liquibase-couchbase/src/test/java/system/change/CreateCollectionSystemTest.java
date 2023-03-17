package system.change;

import org.junit.jupiter.api.Test;

import liquibase.Liquibase;
import lombok.SneakyThrows;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseBucketAssert.assertThat;

public class CreateCollectionSystemTest extends LiquibaseSystemTest {

    @Test
    @SneakyThrows
    void Collection_should_be_created_after_liquibase_execution() {
        Liquibase liquibase = liquibase(CREATE_COLLECTION_TEST_XML);

        liquibase.update();

        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope("travels", TEST_SCOPE);
    }
}
