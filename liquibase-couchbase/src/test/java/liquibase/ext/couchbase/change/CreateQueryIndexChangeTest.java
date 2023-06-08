package liquibase.ext.couchbase.change;

import com.google.common.collect.Lists;
import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.CreateQueryIndexStatement;
import liquibase.ext.couchbase.types.Field;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.CREATE_QUERY_INDEX_TEST_XML;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class CreateQueryIndexChangeTest {

    private final Field ID = new Field("id");
    private final Field COUNTRY = new Field("country");
    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_return_correct_confirmation_message() {
        CreateQueryIndexChange change = new CreateQueryIndexChange();
        String indexName = "testTravelQueryIndex";
        change.setIndexName(indexName);
        assertEquals("Query index \"" + indexName + "\" has been created",
                change.getConfirmationMessage(), "confirmation message doesn't match");
    }

    @Test
    void Changelog_should_contain_correct_types_only() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_QUERY_INDEX_TEST_XML);
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .withFailMessage("Changelog contains wrong types")
                .hasOnlyElementsOfType(CreateQueryIndexChange.class);
    }

    @Test
    void Changelog_should_contain_exact_number_of_changes() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_QUERY_INDEX_TEST_XML);
        assertEquals(1, changeLog.getChangeSets().size(), "Changelog size is wrong");
    }

    @Test
    void Change_should_have_right_properties() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_QUERY_INDEX_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        CreateQueryIndexChange change = (CreateQueryIndexChange) firstOf(changeSet.getChanges());

        assertThat(change.getCollectionName()).isEqualTo("travel-sample");
        assertThat(change.getDeferred()).isTrue();
        assertThat(change.getFields()).hasSize(2);
        assertThat(change.getFields()).containsExactly(ID, COUNTRY);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        CreateQueryIndexChange change = createCreateQueryIndexChange();

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Query index \"%s\" has been created", change.getIndexName());
    }

    @Test
    void Should_generate_statement_correctly() {
        CreateQueryIndexChange change = createCreateQueryIndexChange();

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(CreateQueryIndexStatement.class);

        CreateQueryIndexStatement actualStatement = (CreateQueryIndexStatement) statements[0];
        assertThat(actualStatement.getKeyspace()).isEqualTo(
                keyspace(change.getBucketName(), change.getScopeName(), change.getCollectionName()));
        assertThat(actualStatement.getIndexName()).isEqualTo(change.getIndexName());
        assertThat(actualStatement.isDeferred()).isEqualTo(change.getDeferred());
        assertThat(actualStatement.getNumReplicas()).isEqualTo(change.getNumReplicas());
        assertThat(actualStatement.getFields()).isEqualTo(change.getFields());
    }

    @Test
    void Should_generate_inverse_correctly() {
        CreateQueryIndexChange change = createCreateQueryIndexChange();

        Change[] inverses = change.createInverses();

        assertThat(inverses).hasSize(1);
        assertThat(inverses[0]).isInstanceOf(DropIndexChange.class);

        DropIndexChange inverseChange = (DropIndexChange) inverses[0];
        assertThat(inverseChange.getScopeName()).isEqualTo(change.getScopeName());
        assertThat(inverseChange.getBucketName()).isEqualTo(change.getBucketName());
        assertThat(inverseChange.getCollectionName()).isEqualTo(change.getCollectionName());
        assertThat(inverseChange.getIndexName()).isEqualTo(change.getIndexName());
    }


    @Test
    void Should_generate_correct_checksum() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_QUERY_INDEX_TEST_XML);
        String checkSum = "8:15ff1eafac2404f08f2ad2189d41bc3e";
        assertThat(changeLog.getChangeSets()).first().returns(checkSum, it -> it.generateCheckSum().toString());
    }

    private CreateQueryIndexChange createCreateQueryIndexChange() {
        return CreateQueryIndexChange.builder().bucketName(TEST_BUCKET).scopeName(TEST_SCOPE)
                .collectionName(TEST_COLLECTION).indexName(INDEX).deferred(false)
                .numReplicas(2).fields(Lists.newArrayList(ID)).build();
    }
}
