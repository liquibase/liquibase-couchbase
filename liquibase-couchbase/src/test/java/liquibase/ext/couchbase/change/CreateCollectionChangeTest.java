package liquibase.ext.couchbase.change;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.statement.CreateCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.statement.SqlStatement;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class CreateCollectionChangeTest {

    private static final String collectionName = "travels";

    private DatabaseChangeLog changeLog;
    private ChangeLogProvider changeLogProvider;
    private CouchbaseLiquibaseDatabase database;

    @BeforeEach
    void setUp() {
        database = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(database);
        changeLog = changeLogProvider.load(CREATE_COLLECTION_TEST_XML);
    }

    @Test
    void Expects_confirmation_message_is_create_collection() {
        CreateCollectionChange change = new CreateCollectionChange(TEST_BUCKET,
                TEST_SCOPE, collectionName, false);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("%s has been successfully created", collectionName);
    }

    @Test
    void Should_return_only_CreateCollectionStatement() {
        CreateCollectionChange change = new CreateCollectionChange(TEST_BUCKET,
                TEST_SCOPE, collectionName, false);
        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, collectionName);

        SqlStatement[] sqlStatements = change.generateStatements();

        assertThat(sqlStatements).containsExactly(new CreateCollectionStatement(keyspace, false));
    }


    @Test
    void Create_collection_xml_parses_correctly() {
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .hasOnlyElementsOfType(CreateCollectionChange.class);
    }

    @Test
    void Create_collection_change_has_right_properties() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());

        CreateCollectionChange change = (CreateCollectionChange) firstOf(changeSet.getChanges());

        assertThat(change.getCollectionName()).isEqualTo("travels");
    }

    @Test
    void Create_collection_change_generates_right_checksum() {
        String checkSum = "8:e883f6f8fb99b3427644c6dfe2b610a2";
        assertThat(changeLog.getChangeSets()).first()
                .returns(checkSum, it -> it.generateCheckSum().toString());
    }
}