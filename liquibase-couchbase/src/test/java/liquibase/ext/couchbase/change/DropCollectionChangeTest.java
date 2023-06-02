package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.statement.DropCollectionStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class DropCollectionChangeTest {

    private ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        DropCollectionChange dropCollectionChange = new DropCollectionChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
        DatabaseChangeLog load = changeLogProvider.load(DROP_NOT_CREATED_COLLECTION_CHANGE_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges())
                .map(DropCollectionChange.class::cast)
                .containsExactly(dropCollectionChange);
    }

    @Test
    void Expects_confirmation_message_is_create_correctly() {
        DropCollectionChange change = new DropCollectionChange(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Collection %s has been successfully dropped", TEST_COLLECTION);
    }

    @Test
    void Should_generate_statement_correctly() {
        DropCollectionChange change = DropCollectionChange.builder().collectionName(TEST_COLLECTION)
                .bucketName(TEST_BUCKET).scopeName(TEST_SCOPE).build();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(DropCollectionStatement.class);

        DropCollectionStatement actualStatement = (DropCollectionStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

}