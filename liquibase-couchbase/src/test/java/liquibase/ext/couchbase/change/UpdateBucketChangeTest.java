package liquibase.ext.couchbase.change;

import com.couchbase.client.java.manager.bucket.CompressionMode;
import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.change.utils.BucketUpdateMapper;
import liquibase.ext.couchbase.statement.UpdateBucketStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.UPDATE_BUCKET_TEST_JSON;
import static common.constants.ChangeLogSampleFilePaths.UPDATE_BUCKET_TEST_XML;
import static common.constants.TestConstants.UPDATE_TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
public class UpdateBucketChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_parse_changes_correctly() {
        UpdateBucketChange updateBucketChange = createUpdateBucketChange();

        DatabaseChangeLog load = changeLogProvider.load(UPDATE_BUCKET_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(UpdateBucketChange.class::cast)
                .containsExactly(updateBucketChange);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        UpdateBucketChange change = createUpdateBucketChange();

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Bucket <%s> has been updated", change.getBucketName());
    }

    @Test
    void Should_generate_statement_correctly() {
        UpdateBucketChange change = createUpdateBucketChange();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(UpdateBucketStatement.class);

        UpdateBucketStatement actualStatement = (UpdateBucketStatement) statements[0];
        BucketUpdateMapper bucketUpdate = new BucketUpdateMapper(change);
        assertThat(actualStatement.getSettings().toString()).isEqualTo(
                bucketUpdate.bucketSettings().toString()); // Equals of object is not overridden
    }

    @Test
    void Should_parse_changes_correctly_json() {
        UpdateBucketChange updateBucketChange = UpdateBucketChange.builder()
                .bucketName(UPDATE_TEST_BUCKET).compressionMode(CompressionMode.PASSIVE)
                .maxExpiryInHours(2L).numReplicas(1).ramQuotaMB(256L)
                .flushEnabled(false).timeoutInSeconds(17L).build();

        DatabaseChangeLog load = changeLogProvider.load(UPDATE_BUCKET_TEST_JSON);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(UpdateBucketChange.class::cast)
                .containsExactly(updateBucketChange);
    }

    private UpdateBucketChange createUpdateBucketChange() {
        return UpdateBucketChange.builder()
                .bucketName(UPDATE_TEST_BUCKET).compressionMode(CompressionMode.PASSIVE)
                .maxExpiryInHours(2L).numReplicas(1).ramQuotaMB(256L)
                .flushEnabled(false).timeoutInSeconds(18L).build();
    }

}