package liquibase.ext.couchbase.change;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;

import static common.constants.ChangeLogSampleFilePaths.CREATE_PRIMARY_QUERY_INDEX_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class CreatePrimaryQueryIndexChangeTest {

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(CREATE_PRIMARY_QUERY_INDEX_TEST_XML);
    }

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
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(CreatePrimaryQueryIndexChange.class);
    }

    @Test
    void Changelog_should_contain_exact_number_of_changes() {
        assertEquals(1, changeLog.getChangeSets().size(), "Changelog size is wrong");
    }


    @Test
    void Change_should_have_right_properties() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        CreatePrimaryQueryIndexChange change = (CreatePrimaryQueryIndexChange) firstOf(changeSet.getChanges());
        assertThat(change.getCollectionName()).isEqualTo("sample");
        assertThat(change.getDeferred()).isTrue();
    }

    @Test
    void Should_generate_correct_checksum() {
        String checkSum = "8:1986781cf7b9bf3f25da635e885e1a30";
        assertThat(changeLog.getChangeSets()).first().returns(checkSum, it -> it.generateCheckSum().toString());
    }
}
