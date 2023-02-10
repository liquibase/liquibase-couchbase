package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.INSERT_MANY_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

class InsertManyChangeTest {

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
                .hasOnlyElementsOfType(InsertManyChange.class);
    }

    @Test
    void Should_contains_correct_bucket() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        InsertManyChange change = (InsertManyChange) firstOf(changeSet.getChanges());
        assertThat(change.getBucketName()).isEqualTo("testBucket");
    }
}

