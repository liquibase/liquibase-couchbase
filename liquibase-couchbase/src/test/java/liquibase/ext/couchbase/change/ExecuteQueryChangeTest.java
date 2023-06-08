package liquibase.ext.couchbase.change;

import com.google.common.collect.Lists;
import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.ExecuteQueryStatement;
import liquibase.ext.couchbase.types.Param;
import liquibase.statement.SqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.EXECUTE_QUERY_CHANGE_TEST_XML;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class ExecuteQueryChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;
    private final String query = "DELETE FROM `testBucket`.testScope.testCollection WHERE META().id = $id";
    private List<Param> paramList = Lists.newArrayList(new Param("id", "abcd"));

    @Test
    void Should_parse_changes_correctly() {
        ExecuteQueryChange executeQueryChange = new ExecuteQueryChange(query, paramList);
        DatabaseChangeLog load = changeLogProvider.load(EXECUTE_QUERY_CHANGE_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges()).map(ExecuteQueryChange.class::cast)
                .containsExactly(executeQueryChange);
    }

    @Test
    void Expects_confirmation_message_is_created_correctly_non_primary() {
        ExecuteQueryChange change = new ExecuteQueryChange();
        change.setQuery(query);
        change.setParams(paramList);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("Query %s has been successfully executed", change.getQuery());
    }

    @Test
    void Should_generate_statement_correctly() {
        ExecuteQueryChange change = new ExecuteQueryChange(query, paramList);

        SqlStatement[] statements = change.generateStatements();

        assertThat(statements).hasSize(1);
        assertThat(statements[0]).isInstanceOf(ExecuteQueryStatement.class);

        ExecuteQueryStatement actualStatement = (ExecuteQueryStatement) statements[0];
        assertThat(actualStatement.getQuery()).isEqualTo(change.getQuery());
        assertThat(actualStatement.getParams()).isEqualTo(change.getParams());
    }

}