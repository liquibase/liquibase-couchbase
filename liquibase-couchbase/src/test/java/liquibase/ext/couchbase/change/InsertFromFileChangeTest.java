package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.types.File;
import liquibase.ext.couchbase.types.ImportType;
import liquibase.ext.couchbase.types.KeyProviderType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.INSERT_EXPRESSION_KEY_GENERATOR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_FROM_FILE_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_INCREMENT_KEY_GENERATOR_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.INSERT_UID_KEY_GENERATOR_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class InsertFromFileChangeTest {

    private static final String TEST_FILE_NAME_LINES = "testLines.json";
    private static final String TEST_FILE_NAME_LIST = "testList.json";
    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_have_correct_change_type() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_FROM_FILE_TEST_XML);
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(InsertDocumentsChange.class);
    }

    @Test
    void Should_contains_specific_documents() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_FROM_FILE_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).isEmpty();
        assertThat(change.getFile()).isNotNull();
        assertThat(change.getFile().getFilePath()).contains(TEST_FILE_NAME_LINES);
        assertThat(change.getFile().getImportType()).isEqualTo(ImportType.LINES);
    }

    @Test
    void Should_contains_uid_key_provider() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_UID_KEY_GENERATOR_TEST_XML);

        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).isEmpty();
        assertThat(change.getFile()).isNotNull();
        assertThat(change.getFile().getKeyProviderType()).isEqualTo(KeyProviderType.UID);
    }

    @Test
    void Should_read_docs_list_mode() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_FROM_FILE_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) changeSet.getChanges().get(1);
        assertThat(change.getDocuments()).isEmpty();
        assertThat(change.getFile()).isNotNull();
        assertThat(change.getFile().getFilePath()).contains(TEST_FILE_NAME_LIST);
        assertThat(change.getFile().getImportType()).isEqualTo(ImportType.LIST);
    }

    @Test
    void Should_contains_incremental_key_provider() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_INCREMENT_KEY_GENERATOR_TEST_XML);

        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).isEmpty();
        assertThat(change.getFile()).isNotNull();
        assertThat(change.getFile().getKeyProviderType()).isEqualTo(KeyProviderType.INCREMENT);
    }

    @Test
    void Should_contains_expression_key_provider() {
        DatabaseChangeLog changeLog = changeLogProvider.load(INSERT_EXPRESSION_KEY_GENERATOR_TEST_XML);

        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).isEmpty();
        File file = change.getFile();
        assertThat(file).isNotNull();
        assertThat(file.getKeyProviderExpression()).isNotEmpty();
        assertThat(file.getKeyProviderType()).isEqualTo(KeyProviderType.EXPRESSION);
    }
}

