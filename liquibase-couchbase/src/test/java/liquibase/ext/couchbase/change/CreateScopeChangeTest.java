package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.CreateScopeStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.CREATE_SCOPE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class CreateScopeChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_parse_changes_correctly() {
        CreateScopeChange createScopeChange = new CreateScopeChange(TEST_BUCKET, TEST_SCOPE);
        DatabaseChangeLog load = changeLogProvider.load(CREATE_SCOPE_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(CreateScopeChange.class::cast)
                .containsExactly(createScopeChange);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly_non_primary() {
        CreateScopeChange change = new CreateScopeChange();
        change.setScopeName(TEST_SCOPE);
        change.setBucketName(TEST_BUCKET);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Scope %s has been successfully created", change.getScopeName());
    }

    @Test
    void Should_generate_statement_correctly() {
        CreateScopeChange change = new CreateScopeChange(TEST_BUCKET, TEST_SCOPE);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(CreateScopeStatement.class);

        CreateScopeStatement actualStatement = (CreateScopeStatement) statements[0];
        assertThat(actualStatement.getScopeName()).isEqualTo(change.getScopeName());
        assertThat(actualStatement.getBucketName()).isEqualTo(change.getBucketName());
    }

    @Test
    void Should_generate_inverse_correctly() {
        CreateScopeChange change = new CreateScopeChange(TEST_BUCKET, TEST_SCOPE);

        Change[] inverses = change.createInverses();

        assertThat(inverses).hasSize(1);
        assertThat(inverses[0]).isInstanceOf(DropScopeChange.class);

        DropScopeChange inverseChange = (DropScopeChange) inverses[0];
        assertThat(inverseChange.getScopeName()).isEqualTo(change.getScopeName());
        assertThat(inverseChange.getBucketName()).isEqualTo(change.getBucketName());
    }

}