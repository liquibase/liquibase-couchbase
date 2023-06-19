package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.DropBucketStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_JSON;
import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.DROP_BUCKET_TEST_YML;
import static common.constants.TestConstants.NEW_TEST_BUCKET;
import static common.constants.TestConstants.TEST_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
public class DropBucketChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_parse_changes_correctly() {
        DropBucketChange dropBucketChange = new DropBucketChange(NEW_TEST_BUCKET);
        DatabaseChangeLog load = changeLogProvider.load(DROP_BUCKET_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(DropBucketChange.class::cast)
                .containsExactly(dropBucketChange);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        DropBucketChange change = new DropBucketChange(TEST_BUCKET);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("The '%s' bucket has been dropped successfully", TEST_BUCKET);
    }

    @Test
    void Should_generate_statement_correctly() {
        DropBucketChange change = new DropBucketChange(TEST_BUCKET);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(DropBucketStatement.class);
    }

    @Test
    void Should_parse_json_changes_correctly() {
        DropBucketChange dropBucketChange = new DropBucketChange(NEW_TEST_BUCKET);
        DatabaseChangeLog load = changeLogProvider.load(DROP_BUCKET_TEST_JSON);
        ChangeSet changeSet = firstOf(load.getChangeSets());
        assertThat(changeSet.getChanges()).map(DropBucketChange.class::cast)
                .containsExactly(dropBucketChange);
    }

    @Test
    void Should_parse_yaml_changes_correctly() {
        DropBucketChange dropBucketChange = new DropBucketChange(NEW_TEST_BUCKET);
        DatabaseChangeLog load = changeLogProvider.load(DROP_BUCKET_TEST_YML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(DropBucketChange.class::cast)
                .containsExactly(dropBucketChange);
    }

}