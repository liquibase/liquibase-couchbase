package system.change;

import common.matchers.CouchbaseClusterAssert;
import liquibase.Liquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_BUCKET_TEST_XML;

public class CreateBucketSystemTest extends LiquibaseSystemTest {

    @Test
    @SneakyThrows
    void Bucket_should_be_created() {
        Liquibase liquibase = liquibase(CREATE_BUCKET_TEST_XML);
        liquibase.update();
        CouchbaseClusterAssert.assertThat(cluster).hasBucket("createBucketSystemTest");
    }
}
