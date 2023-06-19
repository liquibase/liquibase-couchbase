package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import liquibase.ContextExpression;
import liquibase.Labels;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.changelog.Context;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ServiceProvider;
import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static liquibase.ext.couchbase.operator.ChangeLogOperator.SELECT_ALL_CHANGELOGS_N1QL;
import static liquibase.ext.couchbase.operator.ChangeLogOperator.SELECT_LAST_CHANGE_SET_ID_N1QL;
import static liquibase.ext.couchbase.operator.ChangeLogOperator.SELECT_LAST_ORDER_N1QL;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
class ChangeLogOperatorTest {

    @Mock
    private ServiceProvider serviceProvider;

    @Mock
    private CouchbaseLiquibaseDatabase database;

    @Mock
    private CouchbaseConnection connection;

    @Mock
    private Cluster cluster;

    @Mock
    private QueryIndexManager queryIndexManager;

    @Mock
    private Scope testScope;

    @Mock
    private Collection collection;

    @Mock
    private QueryResult queryResult;

    @Mock
    private ChangeSet changeSet;

    @InjectMocks
    private ChangeLogOperator changeLogOperator;

    @Test
    void Should_create_changelog_collection() {
        when(serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION)).thenReturn(testScope);
        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster()).thenReturn(cluster);
        when(cluster.queryIndexes()).thenReturn(queryIndexManager);
        when(testScope.name()).thenReturn(TEST_SCOPE);
        when(testScope.bucketName()).thenReturn(TEST_BUCKET);

        changeLogOperator.createChangeLogCollection();

        verify(queryIndexManager).createPrimaryIndex(eq(TEST_BUCKET), any(CreatePrimaryQueryIndexOptions.class));
    }

    @Test
    void Should_insert_changelog() {
        CouchbaseChangeLog changeLog = CouchbaseChangeLog.builder()
                .fileName("file")
                .id("id")
                .author("author").build();

        when(serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION)).thenReturn(collection);

        changeLogOperator.insertChangeLog(changeLog);

        verify(collection).insert("file::id::author", changeLog);
    }

    @Test
    void Should_return_last_executed_changeset() {
        int lastExecuted = 3;
        JsonObject lastChangeset = JsonObject.create().put("orderExecuted", lastExecuted);

        when(serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION)).thenReturn(testScope);
        when(testScope.query(eq(SELECT_LAST_ORDER_N1QL), any(QueryOptions.class))).thenReturn(queryResult);
        when(queryResult.rowsAsObject()).thenReturn(singletonList(lastChangeset));

        assertEquals(lastExecuted, changeLogOperator.findLastOrderExecuted());
    }

    @Test
    void should_return_last_executed_changeset_when_empty() {
        when(serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION)).thenReturn(testScope);
        when(testScope.query(eq(SELECT_LAST_ORDER_N1QL), any(QueryOptions.class))).thenReturn(queryResult);
        when(queryResult.rowsAsObject()).thenReturn(emptyList());

        assertEquals(0, changeLogOperator.findLastOrderExecuted());
    }

    @Test
    void Should_return_all_changelogs() {
        Context context = new Context();
        context.setOriginalString("someContext");
        CouchbaseChangeLog changeLog1 = createChangeLog("file1", context);
        CouchbaseChangeLog changeLog2 = createChangeLog("file2", context);

        List<RanChangeSet> expectedChangesets = new ArrayList<>();
        RanChangeSet ranChangeSet1 = createChangeset(changeLog1);
        RanChangeSet ranChangeSet2 = createChangeset(changeLog2);
        expectedChangesets.add(ranChangeSet1);
        expectedChangesets.add(ranChangeSet2);

        when(serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION)).thenReturn(testScope);
        when(testScope.query(eq(SELECT_ALL_CHANGELOGS_N1QL), any(QueryOptions.class))).thenReturn(queryResult);
        when(queryResult.rowsAs(CouchbaseChangeLog.class)).thenReturn(Arrays.asList(changeLog1, changeLog2));

        assertEquals(expectedChangesets, changeLogOperator.getAllChangeLogs());
    }

    private static CouchbaseChangeLog createChangeLog(String fileName, Context context) {
        return CouchbaseChangeLog.builder()
                .fileName(fileName)
                .id("id")
                .author("author")
                .dateExecuted("2023.06.02 24:59:59")
                .context(context)
                .labels(emptySet())
                .build();
    }

    @NonNull
    private static RanChangeSet createChangeset(CouchbaseChangeLog changeLog) {
        Date date = new Date(2023, Calendar.JULY, 2, 23, 59, 59);
        return new RanChangeSet(
                changeLog.getFileName(),
                changeLog.getId(),
                changeLog.getAuthor(),
                null,
                date,
                null,
                null,
                null,
                null,
                new ContextExpression(changeLog.getContext().getOriginalString()),
                new Labels(changeLog.getLabels()),
                null
        );
    }

    @Test
    void Should_remove_changeset_from_history() {
        when(changeSet.getFilePath()).thenReturn("file");
        when(changeSet.getId()).thenReturn("id");
        when(changeSet.getAuthor()).thenReturn("author");
        when(serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION)).thenReturn(collection);

        changeLogOperator.removeChangeSetFromHistory(changeSet);

        verify(collection).remove("file::id::author");
    }

    @Test
    void Should_tag_last_changeset() {
        String changesetId = "id1";
        String tag = "version-1.0";
        JsonObject jsonObject = JsonObject.create().put("id", changesetId);

        when(serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION)).thenReturn(testScope);
        when(serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION)).thenReturn(collection);
        when(testScope.query(eq(SELECT_LAST_CHANGE_SET_ID_N1QL), any(QueryOptions.class))).thenReturn(queryResult);
        when(queryResult.rowsAsObject()).thenReturn(singletonList(jsonObject));

        changeLogOperator.tagLastChangeSet(tag);

        verify(collection).mutateIn(eq(changesetId), any(List.class));
    }

    @Test
    void Should_create_changeLogOperator_when_databaseProvided() {
        assertThatCode(() -> new ChangeLogOperator(database)).doesNotThrowAnyException();
    }

}
