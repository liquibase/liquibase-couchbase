package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.statement.DropIndexStatement;
import liquibase.ext.couchbase.statement.DropPrimaryIndexStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.DROP_INDEX_TEST_XML;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class DropIndexChangeTest {

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(DROP_INDEX_TEST_XML);
    }

    @Test
    void Should_have_correct_change_type() {
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(DropIndexChange.class);
    }

    @Test
    void Should_contains_correct_bucket() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        DropIndexChange change = (DropIndexChange) firstOf(changeSet.getChanges());
        assertThat(change.getBucketName()).isEqualTo("testBucket");
    }

    @Test
    void Expects_confirmation_message_is_create_correctly_non_primary() {
        DropIndexChange change = DropIndexChange.builder().indexName(INDEX).bucketName(TEST_BUCKET)
                .scopeName(TEST_SCOPE).collectionName(TEST_COLLECTION).isPrimary(false).build();

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Index %s dropped for bucket %s", change.getIndexName(), change.getBucketName());
    }

    @Test
    void Expects_confirmation_message_is_create_correctly_primary() {
        DropIndexChange change = new DropIndexChange(true, INDEX, TEST_BUCKET, TEST_COLLECTION, TEST_SCOPE);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Primary index dropped for bucket %s", change.getBucketName());
    }

    @Test
    void Should_generate_statement_correctly_primary_index() {
        DropIndexChange change = DropIndexChange.builder().indexName(INDEX).bucketName(TEST_BUCKET)
                .scopeName(TEST_SCOPE).collectionName(TEST_COLLECTION).isPrimary(true).build();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(DropPrimaryIndexStatement.class);

        DropPrimaryIndexStatement actualStatement = (DropPrimaryIndexStatement) statements[0];
        assertThat(actualStatement.getIndexName()).isEqualTo(change.getIndexName());
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

    @Test
    void Should_generate_statement_correctly_non_primary_index() {
        DropIndexChange change = DropIndexChange.builder().indexName(INDEX).bucketName(TEST_BUCKET)
                .scopeName(TEST_SCOPE).collectionName(TEST_COLLECTION).isPrimary(false).build();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(DropIndexStatement.class);

        DropIndexStatement actualStatement = (DropIndexStatement) statements[0];
        assertThat(actualStatement.getIndexName()).isEqualTo(change.getIndexName());
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }
}

