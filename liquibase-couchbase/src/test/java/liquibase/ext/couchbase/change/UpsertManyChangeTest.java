package liquibase.ext.couchbase.change;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import static common.constants.ChangeLogSampleFilePaths.UPSERT_MANY_TEST_XML;
import static org.mockito.Mockito.mock;

public class UpsertManyChangeTest {

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(UPSERT_MANY_TEST_XML);
    }

    @Test
    void nameTest() {
        List<ChangeSet> changeSets = changeLog.getChangeSets();
        System.out.println(changeSets);

    }
}