package liquibase.ext.couchbase.change;

import com.couchbase.client.java.manager.bucket.CompressionMode;
import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.ChangeLogSampleFilePaths.UPDATE_BUCKET_TEST_XML;
import static common.constants.TestConstants.UPDATE_TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class UpdateBucketChangeTest {

    private ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        UpdateBucketChange updateBucketChange = UpdateBucketChange.builder()
                .bucketName(UPDATE_TEST_BUCKET).compressionMode(CompressionMode.PASSIVE)
                .maxExpiryInHours(2L).numReplicas(1).ramQuotaMB(256L)
                .flushEnabled(false).timeoutInSeconds(18L).build();

        DatabaseChangeLog load = changeLogProvider.load(UPDATE_BUCKET_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(UpdateBucketChange.class::cast)
                .containsExactly(updateBucketChange);
    }

}