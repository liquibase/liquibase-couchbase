package system.change;

import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import system.LiquiBaseSystemTest;

import static common.constants.ChangeLogSampleFilePaths.*;
import static common.constants.TestConstants.*;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DropCollectionSystemTest extends LiquiBaseSystemTest {

    private final BucketOperator bucketOperator = new BucketOperator(getBucket());

    @Test
    @SneakyThrows
    void Should_drop_collection_when_exists() {
        String collectionName = "dropExistingCollection";
        bucketOperator.createCollection(collectionName, TEST_SCOPE);

        Liquibase liquibase = liquiBase(DROP_EXISTING_COLLECTION_TEST_XML);

        liquibase.update();

        assertThat(cluster.bucket(TEST_BUCKET)).hasNoCollectionInScope(collectionName, TEST_SCOPE);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_bucket_not_exists() {
        Liquibase liquibase = liquiBase(DROP_COLLECTION_IN_NOT_CREATED_BUCKET_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);
    }

    @Test
    @SneakyThrows
    void Should_throw_error_when_scope_not_exists() {
        Liquibase liquibase = liquiBase(DROP_COLLECTION_IN_NOT_CREATED_SCOPE_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);
    }

}