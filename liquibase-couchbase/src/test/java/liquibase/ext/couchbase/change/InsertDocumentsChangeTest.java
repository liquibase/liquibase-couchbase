package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.types.Document;

import static common.constants.ChangeLogSampleFilePaths.INSERT_MANY_TEST_XML;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class InsertDocumentsChangeTest {

    private final Document DOC_1 = document("id1", "{key:value}", DataType.JSON);
    private final Document DOC_2 = document("id2", "{key2:value2}", DataType.JSON);
    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(INSERT_MANY_TEST_XML);
    }

    @Test
    void Should_have_correct_change_type() {
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(InsertDocumentsChange.class);
    }

    @Test
    void Should_contains_correct_bucket() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getBucketName()).isEqualTo("testBucket");
    }

    @Test
    void Should_contains_specific_documents() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertDocumentsChange change = (InsertDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).containsExactly(DOC_1, DOC_2);
    }
}

