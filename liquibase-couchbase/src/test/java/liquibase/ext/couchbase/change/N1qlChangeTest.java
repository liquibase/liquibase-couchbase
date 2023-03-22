package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_SQL_TEST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class N1qlChangeTest {

    private ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        N1qlChange n1qlChange = new N1qlChange(CREATE_COLLECTION_SQL_TEST, false);
        DatabaseChangeLog load = changeLogProvider.load(CREATE_COLLECTION_SQL_TEST);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(N1qlChange.class::cast)
                .containsExactly(n1qlChange);
    }


}