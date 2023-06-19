package liquibase.ext.couchbase.change;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.statement.CouchbaseSqlStatement;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_SQL_TEST;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENT_ROLLBACK_SQL_TEST;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENT_SQL_TEST;
import static liquibase.ext.couchbase.change.QueryFileChange.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.internal.util.collections.Iterables.firstOf;

@MockitoSettings(strictness = Strictness.LENIENT)
class QueryFileChangeTest {

    @InjectMocks
    private TestChangeLogProvider changeLogProvider;

    @Test
    void Should_parse_changes_correctly() {
        DatabaseChangeLog load = changeLogProvider.load(CREATE_COLLECTION_SQL_TEST);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges())
                .map(QueryFileChange.class::cast)
                .containsExactly(builder()
                        .path(CREATE_COLLECTION_SQL_TEST)
                        .transactional(false)
                        //.relative(false)
                        .build()
                );
    }

    @Test
    void Should_correct_resolve_relative_path() {
        QueryFileChange change = parseSqlFileChange(INSERT_DOCUMENT_SQL_TEST);

        CouchbaseSqlStatement stmt = (CouchbaseSqlStatement) change.generateStatements()[0];

        assertThat(stmt.getResource().exists()).isTrue();
        assertThat(stmt.isTransactional()).isTrue();
    }

    @Test
    void Should_correct_resolve_absolute_path() {
        QueryFileChange change = parseSqlFileChange(INSERT_DOCUMENT_ROLLBACK_SQL_TEST);

        CouchbaseSqlStatement stmt = (CouchbaseSqlStatement) change.generateStatements()[0];

        assertThat(stmt.getResource().exists()).isTrue();
        assertThat(stmt.isTransactional()).isTrue();
    }

    @Test
    void Expects_confirmation_message_is_created_correctly() {
        QueryFileChange change = parseSqlFileChange(INSERT_DOCUMENT_SQL_TEST);

        String msg = change.getConfirmationMessage();

        assertThat(msg).isEqualTo("The queries located in %s file has been executed successfully", change.getPath());
    }

    private QueryFileChange parseSqlFileChange(String path) {
        DatabaseChangeLog load = changeLogProvider.load(path);
        ChangeSet changeSet = firstOf(load.getChangeSets());
        return (QueryFileChange) firstOf(changeSet.getChanges());
    }

}