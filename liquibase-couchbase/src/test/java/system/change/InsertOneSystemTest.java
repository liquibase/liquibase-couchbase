package system.change;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;

import org.junit.jupiter.api.Test;

import common.matchers.ChangeLogAssert;
import common.matchers.CouchbaseCollectionAssert;
import liquibase.Liquibase;
import liquibase.changelog.ChangeSet;
import liquibase.exception.LiquibaseException;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;
import lombok.SneakyThrows;
import system.LiquiBaseSystemTest;
import static common.constants.ChangeLogSampleFilePaths.INSERT_ONE_2_CHANGESETS_ONE_SUCCESSFULL_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_ONE_BROKEN_TEST_XML;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class InsertOneSystemTest extends LiquiBaseSystemTest {

    private static final BucketOperator bucketOperator = new BucketOperator(getBucket());
    private static final CollectionOperator testCollectionOperator =
            new CollectionOperator(bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
    private static final Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
    private static final ServiceProvider serviceProvider = new ContextServiceProvider(database);
    private static final Scope scope = cluster.bucket(serviceProvider.getServiceBucketName()).scope(DEFAULT_SERVICE_SCOPE);

    @Test
    @SneakyThrows
    void Should_not_insert_new_documents_when_transaction_was_broken() {
        String existingKey = "existingKey";
        testCollectionOperator.insertDoc(existingKey, TEST_DOCUMENT);

        Liquibase liquibase = liquiBase(INSERT_ONE_BROKEN_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        assertThat(collection)
                .hasDocument(existingKey)
                .hasNoDocument("newKey");

        testCollectionOperator.removeDoc(existingKey);
    }

    /**
     * The point of this test is to check that scope of the transaction only 1 changeset, and it doesn't affect others
     */
    @Test
    @SneakyThrows
    void Should_execute_first_changeset_and_write_into_history_only_first_when_second_was_broken() {
        String successfullyCreatedKey = "successfullyCreatedKey";
        String existingKey = "existingKey";
        testCollectionOperator.insertDoc(existingKey, TEST_DOCUMENT);

        Liquibase liquibase = liquiBase(INSERT_ONE_2_CHANGESETS_ONE_SUCCESSFULL_TEST_XML);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        CouchbaseCollectionAssert.assertThat(collection)
                .hasDocument(existingKey)
                .hasDocument(successfullyCreatedKey)
                .hasNoDocument("notCreated");

        ChangeLogAssert.assertThat(scope)
                .hasDocument(changeSet(1))
                .withExecType(ChangeSet.ExecType.EXECUTED);

        ChangeLogAssert.assertThat(scope)
                .hasNoDocument(changeSet(2));

        testCollectionOperator.removeDoc(existingKey);
        testCollectionOperator.removeDoc(successfullyCreatedKey);
    }

    private String changeSet(Integer changeSetNum) {
        return String.format("liquibase/ext/couchbase/insert/" +
                "changelog.insert-one-2-changesets-with-one-broken.test.xml::%s::dmitry", changeSetNum);
    }
}
