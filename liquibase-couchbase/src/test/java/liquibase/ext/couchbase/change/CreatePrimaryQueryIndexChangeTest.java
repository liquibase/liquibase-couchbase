package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.CreatePrimaryQueryIndexStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.CREATE_PRIMARY_QUERY_INDEX_TEST_XML;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class CreatePrimaryQueryIndexChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_return_correct_confirmation_message() {
        CreatePrimaryQueryIndexChange change = new CreatePrimaryQueryIndexChange();
        String indexName = "test";
        change.setIndexName(indexName);
        assertEquals("Primary query index \"" + indexName + "\" has been created",
                change.getConfirmationMessage(), "confirmation message doesn't match");
    }

    @Test
    void Changelog_should_contain_correct_types_only() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_PRIMARY_QUERY_INDEX_TEST_XML);
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(CreatePrimaryQueryIndexChange.class);
    }

    @Test
    void Changelog_should_contain_exact_number_of_changes() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_PRIMARY_QUERY_INDEX_TEST_XML);
        assertEquals(1, changeLog.getChangeSets().size(), "Changelog size is wrong");
    }


    @Test
    void Change_should_have_right_properties() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_PRIMARY_QUERY_INDEX_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        CreatePrimaryQueryIndexChange change = (CreatePrimaryQueryIndexChange) firstOf(changeSet.getChanges());
        assertThat(change.getCollectionName()).isEqualTo("travel-sample");
        assertThat(change.getDeferred()).isTrue();
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        CreatePrimaryQueryIndexChange change = createCreatePrimaryQueryIndexChange();

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Primary query index \"%s\" has been created", change.getIndexName());
    }

    @Test
    void Should_generate_statement_correctly() {
        CreatePrimaryQueryIndexChange change = createCreatePrimaryQueryIndexChange();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(CreatePrimaryQueryIndexStatement.class);

        CreatePrimaryQueryIndexStatement actualStatement = (CreatePrimaryQueryIndexStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
    }

    @Test
    void Should_generate_inverse_correctly() {
        CreatePrimaryQueryIndexChange change = createCreatePrimaryQueryIndexChange();

        Change[] inverses = change.createInverses();

        assertThat(inverses).hasSize(1);
        assertThat(inverses[0]).isInstanceOf(DropIndexChange.class);

        DropIndexChange inverseChange = (DropIndexChange) inverses[0];
        assertThat(inverseChange.getScopeName()).isEqualTo(change.getScopeName());
        assertThat(inverseChange.getBucketName()).isEqualTo(change.getBucketName());
        assertThat(inverseChange.getCollectionName()).isEqualTo(change.getCollectionName());
        assertThat(inverseChange.getIndexName()).isEqualTo(change.getIndexName());
    }

    @Test
    void Should_generate_correct_checksum() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_PRIMARY_QUERY_INDEX_TEST_XML);
        String checkSum = "8:74f679f1be9caf4a748faa3b62114cfe";
        assertThat(changeLog.getChangeSets()).first().returns(checkSum, it -> it.generateCheckSum().toString());
    }

    private CreatePrimaryQueryIndexChange createCreatePrimaryQueryIndexChange() {
        return CreatePrimaryQueryIndexChange.builder().bucketName(TEST_BUCKET).scopeName(TEST_SCOPE)
                .collectionName(TEST_COLLECTION).indexName(INDEX).deferred(false)
                .numReplicas(2).build();
    }
}
