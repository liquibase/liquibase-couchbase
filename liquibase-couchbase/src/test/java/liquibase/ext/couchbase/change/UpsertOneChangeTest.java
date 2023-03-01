package liquibase.ext.couchbase.change;

import liquibase.ext.couchbase.types.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.TestChangeLogProvider;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.ext.couchbase.changelog.ChangeLogProvider;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.types.Document;

import static common.constants.ChangeLogSampleFilePaths.UPSERT_ONE_TEST_XML;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.constants.TestConstants.TRAVELS_BUCKET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.internal.util.collections.Iterables.firstOf;

public class UpsertOneChangeTest {

    private ChangeLogProvider changeLogProvider;
    private final Document doc = Document.document(TEST_ID, "{key:value}", DataType.JSON);

    @BeforeEach
    void setUp() {
        CouchbaseLiquibaseDatabase db = mock(CouchbaseLiquibaseDatabase.class);
        changeLogProvider = new TestChangeLogProvider(db);
    }

    @Test
    void Should_parse_changes_correctly() {
        DatabaseChangeLog load = changeLogProvider.load(UPSERT_ONE_TEST_XML);
        ChangeSet changeSet = firstOf(load.getChangeSets());

        assertThat(changeSet.getChanges())
                .map(UpsertOneChange.class::cast)
                .containsExactly(
                        upsertOneChange(DEFAULT_SCOPE, TEST_COLLECTION),
                        upsertOneChange(DEFAULT_SCOPE, TEST_COLLECTION),
                        upsertOneChange(TEST_SCOPE, TEST_COLLECTION)
                );
    }

    private UpsertOneChange upsertOneChange(String scopeName, String collectionName) {
        return new UpsertOneChange(TRAVELS_BUCKET, scopeName, collectionName, doc);
    }

}