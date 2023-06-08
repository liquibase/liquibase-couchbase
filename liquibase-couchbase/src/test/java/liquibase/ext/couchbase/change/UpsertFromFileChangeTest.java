package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.types.ImportType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.UPSERT_FROM_FILE_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class UpsertFromFileChangeTest {
    private static final String TEST_FILE_NAME = "testLines.json";
    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        changeLog = changeLogProvider.load(UPSERT_FROM_FILE_TEST_XML);
    }

    @Test
    void Should_not_contains_documents() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        UpsertDocumentsChange change = (UpsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).isEmpty();
    }

    @Test
    void Should_contains_specific_document() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        UpsertDocumentsChange change = (UpsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).isEmpty();
        assertThat(change.getFile()).isNotNull();
        assertThat(change.getFile().getFilePath()).contains(TEST_FILE_NAME);
        assertThat(change.getFile().getImportType()).isEqualTo(ImportType.LINES);
    }
}