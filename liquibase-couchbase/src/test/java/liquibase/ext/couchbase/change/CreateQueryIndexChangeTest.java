package liquibase.ext.couchbase.change;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import common.TestChangeLogProvider;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.CREATE_QUERY_INDEX_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class CreateQueryIndexChangeTest {

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(CREATE_QUERY_INDEX_TEST_XML);
    }

    @Test
    void Should_return_correct_confirmation_message() {
        CreateQueryIndexChange change = new CreateQueryIndexChange();
        String indexName = "test";
        change.setIndexName(indexName);
        Assertions.assertEquals("Query index \"" + indexName + "\" has been created",
                change.getConfirmationMessage(), "confirmation message doesn't match");
    }

    @Test
    void Changelog_should_contain_correct_types_only() {
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(CreateQueryIndexChange.class);
    }

    @Test
    void Changelog_should_contain_exact_number_of_changes() {
        Assertions.assertEquals(1, changeLog.getChangeSets().size(), "Changelog size is wrong");
    }

    @Test
    void Change_should_have_right_properties() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        CreateQueryIndexChange change = (CreateQueryIndexChange) firstOf(changeSet.getChanges());
        assertThat(change.getCollectionName()).isEqualTo("travel-sample");
        assertThat(change.getDeferred()).isTrue();
    }

    @Test
    void Should_generate_correct_checksum() {
        String checkSum = "8:8dc095047756951c487d4c1266008912";
        assertThat(changeLog.getChangeSets()).first().returns(checkSum, it -> it.generateCheckSum().toString());
    }
}
