package system.change;

import static common.constants.ChangeLogSampleFilePaths.EXECUTE_QUERY_TEST_XML;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;

import common.matchers.CouchBaseBucketAssert;
import liquibase.Liquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

public class ExecuteQuerySystemTest extends LiquibaseSystemTest {

  @Test
  @SneakyThrows
  void Query_should_be_executed() {
    Liquibase liquibase = liquibase(EXECUTE_QUERY_TEST_XML);
    liquibase.update();
    CouchBaseBucketAssert.assertThat(cluster.bucket(TEST_BUCKET))
        .hasCollectionInScope("test1", DEFAULT_SCOPE);
      bucketOperator.dropCollectionInDefaultScope("test1");
  }
}
