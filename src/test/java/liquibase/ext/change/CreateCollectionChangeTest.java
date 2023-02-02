package liquibase.ext.change;

import static liquibase.common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import liquibase.ext.statement.CreateCollectionStatement;

import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.statement.SqlStatement;

public class CreateCollectionChangeTest extends CouchbaseChangeTestCase {

    private static final String collectionName = "travels";

    private DatabaseChangeLog changeLog;
    private CreateCollectionChange createCollectionChange;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();
        changeLog = changeLogProvider.load(CREATE_COLLECTION_TEST_XML);
    }

    @Test
    void expects_confirmation_message_is_create_collection() {
        createCollectionChange = new CreateCollectionChange(collectionName);

        String confirmationMessage = createCollectionChange.getConfirmationMessage();

        assertThat(confirmationMessage).isEqualTo("%s has successfully created", collectionName);
    }

    @Test
    void generate_statement_returns_only_create_collection_statement() {
        createCollectionChange = new CreateCollectionChange(collectionName);

        SqlStatement[] sqlStatements = createCollectionChange.generateStatements(database);

        assertThat(sqlStatements).containsExactly(new CreateCollectionStatement(collectionName));
    }


    @Test
    void create_collection_xml_parses_correctly() {
        assertThat(changeLog.getChangeSets())
                .flatMap(ChangeSet::getChanges)
                .hasOnlyElementsOfType(CreateCollectionChange.class);
    }

    @Test
    void create_collection_change_has_right_properties() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());

        CreateCollectionChange change = (CreateCollectionChange) firstOf(changeSet.getChanges());

        assertThat(change.getCollectionName()).isEqualTo("travels");
    }

    @Test
    void create_collection_change_generates_right_checksum() {
        String checkSum = "8:20028d34fddf3f9cf005ae56908317b8";
        assertThat(changeLog.getChangeSets()).first()
                .returns(checkSum, it -> it.generateCheckSum().toString());
    }
}