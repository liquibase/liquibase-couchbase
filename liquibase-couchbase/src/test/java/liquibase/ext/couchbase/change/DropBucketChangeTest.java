package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_JSON;
import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_YML;
import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class DropBucketChangeTest {

    private ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        DropBucketChange dropBucketChange = new DropBucketChange(NEW_TEST_BUCKET, true);
        DatabaseChangeLog load = changeLogProvider.load(DROP_BUCKET_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(DropBucketChange.class::cast)
            .containsExactly(dropBucketChange);
    }

    @Test
    void Should_parse_json_changes_correctly() {
        DropBucketChange dropBucketChange = new DropBucketChange(NEW_TEST_BUCKET, true);
        DatabaseChangeLog load = changeLogProvider.load(DROP_BUCKET_TEST_JSON);
        ChangeSet changeSet = firstOf(load.getChangeSets());
        assertThat(changeSet.getChanges()).map(DropBucketChange.class::cast)
                .containsExactly(dropBucketChange);
    }

    @Test
    void Should_parse_yaml_changes_correctly() {
        DropBucketChange dropBucketChange = new DropBucketChange(NEW_TEST_BUCKET, true);
        DatabaseChangeLog load = changeLogProvider.load(DROP_BUCKET_TEST_YML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(DropBucketChange.class::cast)
                .containsExactly(dropBucketChange);
    }

}