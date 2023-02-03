package liquibase.ext.change;

import static liquibase.common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.common.TestChangeLogProvider;
import liquibase.ext.changelog.ChangeLogProvider;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.statement.CreateCollectionStatement;
import liquibase.statement.SqlStatement;

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
        CreateCollectionChange change = new CreateCollectionChange(collectionName);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("%s has been successfully created", collectionName);
    }

    @Test
    void Should_return_only_CreateCollectionStatement() {
        CreateCollectionChange change = new CreateCollectionChange(collectionName);

        SqlStatement[] sqlStatements = change.generateStatements(database);

        assertThat(sqlStatements).containsExactly(new CreateCollectionStatement(collectionName));
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
        String checkSum = "8:20028d34fddf3f9cf005ae56908317b8";
        assertThat(changeLog.getChangeSets()).first()
                .returns(checkSum, it -> it.generateCheckSum().toString());
    }
}