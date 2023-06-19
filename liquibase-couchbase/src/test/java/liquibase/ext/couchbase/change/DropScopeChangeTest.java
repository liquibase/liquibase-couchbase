package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.statement.DropScopeStatement;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.DROP_SCOPE_CHANGE_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class DropScopeChangeTest {

    private ChangeLogProvider changeLogProvider;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        DropScopeChange dropScopeChange = new DropScopeChange(TEST_BUCKET, TEST_SCOPE);
        DatabaseChangeLog load = changeLogProvider.load(DROP_SCOPE_CHANGE_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(DropScopeChange.class::cast)
                .containsExactly(dropScopeChange);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly_non_primary() {
        DropScopeChange change = new DropScopeChange();
        change.setScopeName(TEST_SCOPE);
        change.setBucketName(TEST_BUCKET);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Scope %s has been successfully dropped", change.getScopeName());
    }

    @Test
    void Should_generate_statement_correctly() {
        DropScopeChange change = new DropScopeChange(TEST_BUCKET, TEST_SCOPE);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(DropScopeStatement.class);

        DropScopeStatement actualStatement = (DropScopeStatement) statements[0];
        assertThat(actualStatement.getScopeName()).isEqualTo(change.getScopeName());
        assertThat(actualStatement.getBucketName()).isEqualTo(change.getBucketName());
    }

}