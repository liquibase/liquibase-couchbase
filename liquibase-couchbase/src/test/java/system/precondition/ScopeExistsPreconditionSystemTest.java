package system.precondition;

import com.couchbase.client.java.Collection;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.SCOPE_EXISTS_FAILED_PRECONDITION;
import static common.constants.ChangeLogSampleFilePaths.SCOPE_EXISTS_PRECONDITION;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ScopeExistsPreconditionSystemTest extends LiquibaseSystemTest {
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final String DOCUMENT_ID = "scopeTestPreconditionId1";

    @Test
    @SneakyThrows
    void Should_insert_document_when_scope_exists() {
        Liquibase liquibase = liquibase(SCOPE_EXISTS_PRECONDITION);

        liquibase.update();

        assertThat(collection).containsId(DOCUMENT_ID);
        collection.remove(DOCUMENT_ID);
    }

    @Test
    @SneakyThrows
    void Should_not_insert_document_when_scope_not_exists() {
        Liquibase liquibase = liquibase(SCOPE_EXISTS_FAILED_PRECONDITION);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        assertThat(collection).doesNotContainId(DOCUMENT_ID);
    }
}
