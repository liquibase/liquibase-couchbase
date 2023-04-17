package system.change;

import com.couchbase.client.java.Bucket;
import liquibase.Liquibase;
import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.CREATE_SCOPE_TEST;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.matchers.CouchbaseBucketAssert.assertThat;

public class CreateScopeSystemTest extends LiquibaseSystemTest {

    private static final String SCOPE_NAME = "scopeToRollback";
    private static final Bucket BUCKET = cluster.bucket(TEST_BUCKET);

    @Test
    @SneakyThrows
    void Scope_should_be_created_and_rolled_back() {
        Liquibase liquibase = liquibase(CREATE_SCOPE_TEST);

        liquibase.update();
        assertThat(BUCKET).hasScope(SCOPE_NAME);

        liquibase.rollback(1, null);
        assertThat(BUCKET).hasNoScope(SCOPE_NAME);
    }
}
