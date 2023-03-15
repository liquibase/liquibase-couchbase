package system.change;

import com.couchbase.client.java.Collection;
import liquibase.Liquibase;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.INSERT_FROM_FILE_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;


public class InsertFromFileSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);

    @Test
    @SneakyThrows
    void Should_insert_documents() {
        Liquibase liquibase = liquibase(INSERT_FROM_FILE_TEST_XML);

        liquibase.update();

        assertThat(collection).extractingDocument("id1").hasField("value");
    }
}
