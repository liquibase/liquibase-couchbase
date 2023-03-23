package system.change;

import com.couchbase.client.java.Collection;
import liquibase.Liquibase;
import liquibase.ext.couchbase.operator.CollectionOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.EXECUTE_QUERY_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

public class ExecuteQuerySystemTest extends LiquibaseSystemTest {
    CollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    Collection collection = collectionOperator.getCollection();

    @Test
    @SneakyThrows
    void Query_should_be_executed() {
        Liquibase liquibase = liquibase(EXECUTE_QUERY_TEST_XML);
        liquibase.update();
        assertThat(collection).doesNotContainIds(TEST_ID);
    }
}
