package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.context.ErrorContext;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import com.couchbase.client.java.kv.MutationResult;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;
import liquibase.ext.couchbase.database.CouchbaseConnection;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.lockservice.DatabaseChangeLogLock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static common.constants.TestConstants.TEST_BUCKET;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_LOCK_COLLECTION_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
public class CouchbaseLockServiceTest {

    private static final Long WAIT_TIME_MILLIS = 1000L;
    private static final Long RECHECK_TIME_MILLIS = 100L;

    @Mock
    private Collection collection;
    @Mock
    private CouchbaseConnection connection;
    @Mock
    private ContextServiceProvider serviceProvider;
    @Mock
    private CouchbaseLock couchbaseLock;
    @Mock
    private GetResult getResult;

    private CouchbaseLiquibaseDatabase database;

    private CouchbaseLockService lockService;

    @BeforeEach
    void setUp() {
        try (MockedStatic<CouchbaseLiquibaseConfiguration> mockedStatic = Mockito.mockStatic(CouchbaseLiquibaseConfiguration.class)) {
            mockedStatic.when(CouchbaseLiquibaseConfiguration::getChangelogWaitTime)
                    .thenReturn(Duration.ofMillis(WAIT_TIME_MILLIS));
            mockedStatic.when(CouchbaseLiquibaseConfiguration::getChangelogRecheckTime)
                    .thenReturn(Duration.ofMillis(RECHECK_TIME_MILLIS));
            database = new CouchbaseLiquibaseDatabase();
            database.setConnection(connection);
            lockService = new CouchbaseLockService();
            lockService.setDatabase(database);
            lockService.setServiceProvider(serviceProvider);
        }
    }

    @Test
    @DisplayName("Test priority of lock service")
    void testPriority() {
        assertThat(lockService.getPriority()).isEqualTo(PRIORITY_SPECIALIZED);
    }

    @Test
    @DisplayName("Test lock service supports CouchbaseLiquibaseDatabase")
    void testSupport() {
        assertTrue(lockService.supports(new CouchbaseLiquibaseDatabase()));
        assertTrue(lockService.supports(database));
    }

    @Test
    void Should_list_empty_locks_array() {
        DatabaseChangeLogLock[] result = lockService.listLocks();

        assertThat(result).isEmpty();
    }

    @Test
    void Should_init_successful_on_first_call_only() {
        initMocks();

        assertThatCode(() -> lockService.init()).doesNotThrowAnyException();

        boolean hasLock = lockService.hasChangeLogLock();
        assertThat(hasLock).isFalse();

        verify(serviceProvider).getServiceCollection(any());
        reset(serviceProvider);

        assertThatCode(() -> lockService.init()).doesNotThrowAnyException();

        verify(serviceProvider, never()).getServiceCollection(any());
    }

    @Test
    void Should_acquire_lock_successfully_and_do_nothing_if_acquired() {
        acquireLock();

        reset(collection);

        assertThat(lockService.acquireLock()).isTrue();

        verify(collection, never()).insert(any(), any(), any());
    }

    @Test
    void Should_release_lock_successfully() {
        mockLockOwner(lockService.getServiceId());
        acquireLock();

        assertThatCode(() -> lockService.releaseLock()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();
    }

    @Test
    void Should_force_release_lock_successfully() {
        acquireLock();

        assertThatCode(() -> lockService.forceReleaseLock()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();
        verify(collection).remove(TEST_BUCKET);
    }

    @Test
    void Should_release_lock_on_destroy_successfully() {
        mockLockOwner(lockService.getServiceId());

        acquireLock();

        assertThatCode(() -> lockService.destroy()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();

        verify(collection).remove(TEST_BUCKET);
        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_try_release_lock_on_destroy_and_catch_exception() {
        mockLockOwner("Owner");

        acquireLock();

        assertThatCode(() -> lockService.destroy()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_release_lock_on_reset_successfully() {
        mockLockOwner(lockService.getServiceId());

        acquireLock();

        assertThatCode(() -> lockService.reset()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isFalse();

        verify(collection).remove(TEST_BUCKET);
        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_try_release_lock_on_reset_and_catch_exception() {
        mockLockOwner("Owner");

        acquireLock();

        assertThatCode(() -> lockService.reset()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(couchbaseLock).getOwner();
    }

    @Test
    void Should_wait_for_lock_successfully() {
        initMocks();
        prepareMockedCollectionTimeout(3);

        assertThatCode(() -> lockService.waitForLock()).doesNotThrowAnyException();

        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(collection, times(3)).insert(eq(TEST_BUCKET), any(), any());
    }

    @Test
    void Should_fail_while_wait_for_lock_due_to_time_limit() {
        initMocks();
        prepareMockedCollectionTimeout(12);

        assertThatExceptionOfType(LockException.class)
                .isThrownBy(() -> lockService.waitForLock())
                .withMessage("Could not acquire lock");

        assertThat(lockService.hasChangeLogLock()).isFalse();

        verify(collection, atLeast(10)).insert(eq(TEST_BUCKET), any(), any());
    }

    private void initMocks() {
        when(serviceProvider.getServiceCollection(CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue())).thenReturn(collection);
        when(collection.bucketName()).thenReturn(TEST_BUCKET);
    }

    private void acquireLock() {
        initMocks();

        assertThat(lockService.hasChangeLogLock()).isFalse();
        assertThat(lockService.acquireLock()).isTrue();
        assertThat(lockService.hasChangeLogLock()).isTrue();

        verify(collection).insert(any(), any(), any());
    }

    private void mockLockOwner(String owner) {
        when(collection.get(TEST_BUCKET)).thenReturn(getResult);
        when(getResult.contentAs(CouchbaseLock.class)).thenReturn(couchbaseLock);
        when(couchbaseLock.getOwner()).thenReturn(owner);
    }

    private void prepareMockedCollectionTimeout(int times) {
        AtomicInteger incr = new AtomicInteger();
        when(collection.insert(any(), any(), any())).thenAnswer((arg) -> {
            if (incr.incrementAndGet() >= times) {
                return mock(MutationResult.class);
            }
            else {
                throw new DocumentExistsException(mock(ErrorContext.class));
            }
        });

    }
}
