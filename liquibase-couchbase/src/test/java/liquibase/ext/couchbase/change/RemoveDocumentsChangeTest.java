package liquibase.ext.couchbase.change;

import com.google.common.collect.Sets;
import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.RemoveDocumentsStatement;
import liquibase.ext.couchbase.types.Id;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.REMOVE_BY_QUERY_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_MANY_TEST_XML;
import static common.constants.ChangeLogSampleFilePaths.REMOVE_ONE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class RemoveDocumentsChangeTest {
    private final Id ID_1 = new Id("id1");
    private final Id ID_2 = new Id("id2");

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_have_correct_change_type() {
        DatabaseChangeLog changeLog = changeLogProvider.load(REMOVE_MANY_TEST_XML);
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(RemoveDocumentsChange.class);
    }

    @Test
    void Should_contains_correct_bucket() {
        DatabaseChangeLog changeLog = changeLogProvider.load(REMOVE_MANY_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        RemoveDocumentsChange change = (RemoveDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getBucketName()).isEqualTo("testBucket");
    }

    @Test
    void Should_contains_specific_documents() {
        DatabaseChangeLog changeLog = changeLogProvider.load(REMOVE_MANY_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        RemoveDocumentsChange change = (RemoveDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getIds()).containsExactly(ID_1, ID_2);
    }

    @Test
    void Should_contains_specific_document() {
        DatabaseChangeLog changeLog = changeLogProvider.load(REMOVE_ONE_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        RemoveDocumentsChange change = (RemoveDocumentsChange) firstOf(changeSet.getChanges());
        assertThat(change.getIds()).containsExactly(ID_1);
    }

    @Test
    void Should_contains_exactly_one_document_id() {
        DatabaseChangeLog changeLog = changeLogProvider.load(REMOVE_ONE_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        List<Change> changes = changeSet.getChanges();
        assertThat(changes).hasSize(1);
        assertThat(((RemoveDocumentsChange) changes.get(0)).getIds()).hasSize(1);
    }

    @Test
    void Should_contain_where_clause() {
        DatabaseChangeLog changeLog = changeLogProvider.load(REMOVE_BY_QUERY_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        List<Change> changes = changeSet.getChanges();
        assertThat(changes).hasSize(1);
        RemoveDocumentsChange removeDocumentsChange = (RemoveDocumentsChange) changes.get(0);
        assertThat(removeDocumentsChange.getIds()).hasSize(0);
        assertThat(removeDocumentsChange.getWhereCondition()).isEqualTo("test=\"test\"");
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        RemoveDocumentsChange change = createRemoveDocumentChange();

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Documents removed from collection %s", change.getCollectionName());
    }

    @Test
    void Should_generate_statement_correctly() {
        RemoveDocumentsChange change = createRemoveDocumentChange();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(RemoveDocumentsStatement.class);

        RemoveDocumentsStatement actualStatement = (RemoveDocumentsStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getIds()).isEqualTo(change.getIds());

    }

    private RemoveDocumentsChange createRemoveDocumentChange() {
        return RemoveDocumentsChange.builder().bucketName(TEST_BUCKET)
                .scopeName(TEST_SCOPE).collectionName(TEST_COLLECTION)
                .ids(Sets.newHashSet(new Id("id1"), new Id("id2"))).build();
    }

}

