package liquibase.ext.couchbase.change;

import com.couchbase.client.java.kv.StoreSemantics;
import common.TestChangeLogProvider;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.types.DataType;
import liquibase.ext.couchbase.types.Value;
import liquibase.ext.couchbase.types.subdoc.LiquibaseMutateInSpec;
import liquibase.ext.couchbase.types.subdoc.MutateInType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static common.constants.ChangeLogSampleFilePaths.MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENT_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION_3;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class MutateInChangeQueryTest {

    private DatabaseChangeLog changeLog;

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
        ChangeLogProvider provider = new TestChangeLogProvider(database);
        changeLog = provider.load(MUTATE_QUERY_FILTER_IN_REPLACE_DOCUMENT_TEST_XML);
    }

    @Test
    void Should_parse_mutateIn_query_correctly() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());

        assertThat(changeSet.getChanges())
                .map(MutateInQueryChange.class::cast)
                .containsExactly(
                        change(asList(spec(null, "{\"newDocumentField\": \"newDocumentValue\"}", DataType.JSON, MutateInType.REPLACE)))
                );
    }

    @Test
    void Should_has_correct_confirm_msg() {
        ChangeSet changeSet = firstOf(changeLog.getChangeSets());
        Change change = firstOf(changeSet.getChanges());

        assertThat(change.getConfirmationMessage())
                .isEqualTo("MutateInQuery %s operations has been successfully fulfilled", 1);
    }

    private LiquibaseMutateInSpec spec(String path, String value, DataType dataType, MutateInType type) {
        return new LiquibaseMutateInSpec(path, new Value(value, dataType), type);
    }

    private MutateInQueryChange change(List<LiquibaseMutateInSpec> specs) {
        return new MutateInQueryChange(
                "aKey=\"avalue\"",
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION_3,
                null,
                null,
                StoreSemantics.REPLACE,
                specs
        );
    }
}