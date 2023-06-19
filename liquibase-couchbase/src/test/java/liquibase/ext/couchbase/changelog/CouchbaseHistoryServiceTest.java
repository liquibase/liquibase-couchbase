package liquibase.ext.couchbase.changelog;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.manager.bucket.BucketManager;
import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.collection.CollectionManager;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.Labels;
import liquibase.change.core.TagDatabaseChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.operator.ChangeLogOperator;
import liquibase.ext.couchbase.provider.ServiceProvider;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;
import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@MockitoSettings(strictness = Strictness.LENIENT)
class CouchbaseHistoryServiceTest {

    private final ChangeLogOperator changeLogOperator = mock(ChangeLogOperator.class);
    private final ServiceProvider serviceProvider = mock(ServiceProvider.class);
    private final CouchbaseLiquibaseDatabase database = mock(CouchbaseLiquibaseDatabase.class);
    private CouchbaseHistoryService couchbaseHistoryService;

    @BeforeEach
    void setUp() {
        couchbaseHistoryService = new CouchbaseHistoryService();
        couchbaseHistoryService.setDatabase(database);
        couchbaseHistoryService.setChangeLogOperator(changeLogOperator);
        couchbaseHistoryService.setServiceProvider(serviceProvider);

        couchbaseHistoryService.setRanChangeSetList(null);
        couchbaseHistoryService.setLastChangeLogOrder(1);
        couchbaseHistoryService.setInitialized(false);
    }

    @Test
    void Should_return_priority() {
        assertEquals(PRIORITY_SPECIALIZED, couchbaseHistoryService.getPriority());
    }

    @Test
    void Should_init_and_create_collection_when_not_initialized_and_collection_not_created() {
        mockChangeLogCollectionNotExists();

        couchbaseHistoryService.init();

        verify(changeLogOperator).createChangeLogCollection();
        assertTrue(couchbaseHistoryService.isInitialized());
    }

    @Test
    void Should_init_when_not_initialized_and_collection_is_created() {
        mockChangeLogCollectionExists();

        couchbaseHistoryService.init();

        verify(changeLogOperator, never()).createChangeLogCollection();
        assertTrue(couchbaseHistoryService.isInitialized());
    }

    @Test
    void Should_not_init_when_already_initialized() {
        couchbaseHistoryService.setInitialized(true);
        couchbaseHistoryService.init();

        verify(changeLogOperator, never()).createChangeLogCollection();
        verify(database, never()).getConnection();
    }

    @Test
    @SneakyThrows
    void Should_set_exec_type_and_dont_add_changeset_to_list_when_list_null() {
        ChangeSet changeSet = prepareChangeSet();

        couchbaseHistoryService.setExecType(changeSet, ChangeSet.ExecType.EXECUTED);

        verify(changeLogOperator).insertChangeLog(any(CouchbaseChangeLog.class));
        assertNull(couchbaseHistoryService.getRanChangeSetList());

    }

    @Test
    @SneakyThrows
    void Should_set_exec_type_and_add_changeset_to_list() {
        ChangeSet.ExecType execType = ChangeSet.ExecType.EXECUTED;
        couchbaseHistoryService.setRanChangeSetList(new ArrayList<>());

        ChangeSet changeSet = prepareChangeSet();
        RanChangeSet ranChangeSet = new RanChangeSet(changeSet, execType, null, null);
        List<RanChangeSet> expectedRanChangeSets = singletonList(ranChangeSet);

        couchbaseHistoryService.setExecType(changeSet, execType);

        verify(changeLogOperator).insertChangeLog(any(CouchbaseChangeLog.class));
        assertEquals(expectedRanChangeSets, couchbaseHistoryService.getRanChangeSetList());
    }

    @Test
    @SneakyThrows
    void Should_mark_changeset_run_when_tag_changeset() {
        TagDatabaseChange tagDatabaseChange = new TagDatabaseChange();
        tagDatabaseChange.setTag("someTag");
        ChangeSet changeSet = prepareChangeSet();
        changeSet.addChange(tagDatabaseChange);

        couchbaseHistoryService.markChangeSetRun(changeSet, ChangeSet.ExecType.EXECUTED);

        verify(changeLogOperator).insertChangeLog(any(CouchbaseChangeLog.class));
    }

    @Test
    @SneakyThrows
    void Should_not_mark_changeset_run_when_exec_type_ran_before() {
        ChangeSet changeSet = prepareChangeSet();

        couchbaseHistoryService.markChangeSetRun(changeSet, ChangeSet.ExecType.RERAN);

        verify(changeLogOperator, never()).insertChangeLog(any(CouchbaseChangeLog.class));
    }

    @Test
    @SneakyThrows
    void Should_return_all_ran_changesets_when_collection_exists() {
        ChangeSet changeSet = prepareChangeSet();
        RanChangeSet ranChangeSet = new RanChangeSet(changeSet, ChangeSet.ExecType.EXECUTED, null, null);
        List<RanChangeSet> expectedRanChangeSets = singletonList(ranChangeSet);

        mockChangeLogCollectionExists();

        when(changeLogOperator.getAllChangeLogs()).thenReturn(expectedRanChangeSets);

        List<RanChangeSet> returnedChangeLogs = couchbaseHistoryService.getRanChangeSets();

        assertEquals(expectedRanChangeSets, returnedChangeLogs);
    }

    @Test
    @SneakyThrows
    void Should_return_empty_ran_changesets_when_collection_not_exists() {
        mockChangeLogCollectionNotExists();

        List<RanChangeSet> returnedChangeLogs = couchbaseHistoryService.getRanChangeSets();

        assertTrue(returnedChangeLogs.isEmpty());
    }

    @Test
    @SneakyThrows
    void Should_return_the_same_ran_changesets_if_already_returned() {
        ChangeSet changeSet = prepareChangeSet();
        RanChangeSet ranChangeSet = new RanChangeSet(changeSet, ChangeSet.ExecType.EXECUTED, null, null);
        List<RanChangeSet> expectedRanChangeSets = singletonList(ranChangeSet);

        couchbaseHistoryService.setRanChangeSetList(expectedRanChangeSets);

        List<RanChangeSet> returnedChangeLogs = couchbaseHistoryService.getRanChangeSets();

        assertEquals(expectedRanChangeSets, returnedChangeLogs);
        verify(changeLogOperator, never()).getAllChangeLogs();
    }

    @Test
    @SneakyThrows
    void Should_return_ran_changeset_when_collection_exists() {
        ChangeSet changeSet = prepareChangeSet();
        RanChangeSet expectedRanChangeSet = new RanChangeSet(changeSet, ChangeSet.ExecType.EXECUTED, null, null);
        List<RanChangeSet> expectedRanChangeSets = singletonList(expectedRanChangeSet);
        couchbaseHistoryService.setRanChangeSetList(expectedRanChangeSets);

        mockChangeLogCollectionExists();

        RanChangeSet returnedRanChangeset = couchbaseHistoryService.getRanChangeSet(changeSet);

        assertEquals(expectedRanChangeSet, returnedRanChangeset);
    }

    @Test
    @SneakyThrows
    void Should_return_null_when_collection_not_exists() {
        ChangeSet changeSet = prepareChangeSet();

        mockChangeLogCollectionNotExists();

        RanChangeSet returnedRanChangeset = couchbaseHistoryService.getRanChangeSet(changeSet);

        assertNull(returnedRanChangeset);
    }

    @Test
    void Should_tag() {
        String someTag = "someTag";
        couchbaseHistoryService.tag(someTag);

        verify(changeLogOperator).tagLastChangeSet(someTag);
    }

    @Test
    @SneakyThrows
    void Should_remove_from_history_when_ran_chagesets_not_empty() {
        ChangeSet changeSet = prepareChangeSet();
        RanChangeSet ranChangeSet = new RanChangeSet(changeSet, ChangeSet.ExecType.EXECUTED, null, null);
        List<RanChangeSet> ranChangeSets = new ArrayList<>();
        ranChangeSets.add(ranChangeSet);
        couchbaseHistoryService.setRanChangeSetList(ranChangeSets);

        couchbaseHistoryService.removeFromHistory(changeSet);

        verify(changeLogOperator).removeChangeSetFromHistory(changeSet);
        assertTrue(couchbaseHistoryService.getRanChangeSets().isEmpty());
    }

    @Test
    @SneakyThrows
    void Should_remove_from_history_and_not_remove_from_list_when_empty() {
        ChangeSet changeSet = prepareChangeSet();
        List<RanChangeSet> ranChangeSets = mock(List.class);
        couchbaseHistoryService.setRanChangeSetList(ranChangeSets);
        RanChangeSet ranChangeSet = new RanChangeSet(changeSet);

        when(ranChangeSets.isEmpty()).thenReturn(true);

        couchbaseHistoryService.removeFromHistory(changeSet);

        verify(changeLogOperator).removeChangeSetFromHistory(changeSet);
        verify(ranChangeSets, never()).remove(ranChangeSet);
    }

    @Test
    void Should_return_next_sequence_value_when_initialized() {
        couchbaseHistoryService.setLastChangeLogOrder(1);

        assertEquals(2, couchbaseHistoryService.getNextSequenceValue());
    }

    @Test
    void Should_return_next_sequence_value_when_not_initialized() {
        couchbaseHistoryService.setLastChangeLogOrder(null);
        int expectedOrder = 6;

        when(changeLogOperator.findLastOrderExecuted()).thenReturn(5);

        assertEquals(expectedOrder, couchbaseHistoryService.getNextSequenceValue());
        assertEquals(expectedOrder, couchbaseHistoryService.getLastChangeLogOrder());
    }

    @Test
    void Should_return_supports_when_database_name_equals() {
        when(database.getDatabaseProductName()).thenReturn(COUCHBASE_PRODUCT_NAME);

        assertTrue(couchbaseHistoryService.supports(database));
    }

    @Test
    void Should_return_not_supports_when_database_name_not_equals() {
        when(database.getDatabaseProductName()).thenReturn("OtherDatabase");

        assertFalse(couchbaseHistoryService.supports(database));
    }

    private ChangeSet prepareChangeSet() {
        ChangeSet changeSet = new ChangeSet(
                "id",
                "author",
                false,
                false,
                "filePath",
                "context",
                null,
                null
        );
        changeSet.setLabels(new Labels());
        return changeSet;
    }

    private void mockChangeLogCollectionNotExists() {
         CollectionSpec collectionSpec = mockScopeExists();
         when(collectionSpec.name()).thenReturn("another collection");
    }

    private void mockChangeLogCollectionExists() {
        CollectionSpec collectionSpec = mockScopeExists();
        when(collectionSpec.name()).thenReturn(CHANGE_LOG_COLLECTION);
    }

    @NonNull
    private CollectionSpec mockScopeExists() {
        CouchbaseConnection connection = mock(CouchbaseConnection.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        Bucket bucket = mock(Bucket.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        ScopeSpec scopeSpec = mock(ScopeSpec.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        Set<CollectionSpec> collectionSpecs = new HashSet<>();
        CollectionSpec collectionSpec = mock(CollectionSpec.class, withSettings().defaultAnswer(RETURNS_DEEP_STUBS));
        collectionSpecs.add(collectionSpec);

        when(database.getConnection()).thenReturn(connection);
        when(connection.getCluster().buckets().getBucket(SERVICE_BUCKET_NAME)).thenReturn(mock(BucketSettings.class));
        when(connection.getCluster().bucket(SERVICE_BUCKET_NAME)).thenReturn(bucket);
        when(scopeSpec.collections()).thenReturn(collectionSpecs);
        when(bucket.collections().getAllScopes()).thenReturn(singletonList(scopeSpec));
        when(scopeSpec.collections()).thenReturn(collectionSpecs);
        when(collectionSpec.scopeName()).thenReturn(DEFAULT_SERVICE_SCOPE);
        return collectionSpec;
    }

}