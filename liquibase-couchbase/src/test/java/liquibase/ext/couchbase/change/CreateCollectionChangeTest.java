package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.CreateCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;


@MockitoSettings(strictness = Strictness.LENIENT)
public class CreateCollectionChangeTest {

    private static final String collectionName = "travels";

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Expects_confirmation_message_is_create_collection() {
        CreateCollectionChange change = new CreateCollectionChange(TEST_BUCKET,
                TEST_SCOPE, collectionName);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("%s has been successfully created", collectionName);
    }

    @Test
    void Should_return_only_CreateCollectionStatement() {
        CreateCollectionChange change = new CreateCollectionChange(TEST_BUCKET,
                TEST_SCOPE, collectionName);
        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, collectionName);

        SqlStatement[] sqlStatements = change.generateStatements();

        assertThat(sqlStatements).containsExactly(new CreateCollectionStatement(keyspace));
    }


    @Test
    void Create_collection_xml_parses_correctly() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_COLLECTION_TEST_XML);
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .hasOnlyElementsOfType(CreateCollectionChange.class);
    }

    @Test
    void Create_collection_change_has_right_properties() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_COLLECTION_TEST_XML);
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());

        CreateCollectionChange change = (CreateCollectionChange) firstOf(changeSet.getChanges());

        assertThat(change.getCollectionName()).isEqualTo("travels");
    }

    @Test
    void Should_generate_inverse_correctly() {
        CreateCollectionChange change = new CreateCollectionChange(TEST_BUCKET,
                TEST_SCOPE, collectionName);

        Change[] inverses = change.createInverses();

        assertThat(inverses).hasSize(1);
        assertThat(inverses[0]).isInstanceOf(DropCollectionChange.class);

        DropCollectionChange inverseChange = (DropCollectionChange) inverses[0];
        assertThat(inverseChange.getScopeName()).isEqualTo(change.getScopeName());
        assertThat(inverseChange.getBucketName()).isEqualTo(change.getBucketName());
        assertThat(inverseChange.getCollectionName()).isEqualTo(change.getCollectionName());
    }

    @Test
    void Create_collection_change_generates_right_checksum() {
        DatabaseChangeLog changeLog = changeLogProvider.load(CREATE_COLLECTION_TEST_XML);
        String checkSum = "9:94d6436240c000bf7bed7f02624b597a";
        assertThat(changeLog.getChangeSets()).first()
                .returns(checkSum, it -> it.generateCheckSum().toString());
    }
}