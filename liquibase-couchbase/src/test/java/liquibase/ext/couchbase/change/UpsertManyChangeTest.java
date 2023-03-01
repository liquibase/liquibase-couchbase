package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.UPSERT_MANY_TEST_XML;
import static liquibase.ext.couchbase.types.Document.document;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class UpsertManyChangeTest {
    public final Document DOC_1 = document("id1", "{key:value}", DataType.JSON);
    public final Document DOC_2 = document("id2", "{key2:value2}", DataType.JSON);
    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(UPSERT_MANY_TEST_XML);
    }

    @Test
    void Should_contains_documents() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        UpsertManyChange change = (UpsertManyChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).hasSize(2);
    }

    @Test
    void Should_contains_specific_document() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        UpsertManyChange change = (UpsertManyChange) firstOf(changeSet.getChanges());
        assertThat(change.getDocuments()).containsExactly(DOC_1, DOC_2);
    }
}