package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.core.error.context.ErrorContext;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.GetResult;
import liquibase.exception.LockException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MockitoSettings
class CouchbaseChangelogLockerTest {

    @Mock
    private Collection collection;

    @InjectMocks
    private CouchbaseChangelogLocker couchbaseChangelogLocker;

    @Test
    void Should_force_release() {
        String lockId = "lockId";

        couchbaseChangelogLocker.forceRelease(lockId);

        verify(collection).remove(lockId);
    }

    @Test
    void Should_catch_exception_on_force_release() {
        String lockId = "lockId";
        when(collection.remove(lockId)).thenThrow(new RuntimeException("Mocked"));

        assertThatCode(() -> couchbaseChangelogLocker.forceRelease(lockId)).doesNotThrowAnyException();
    }

    @Test
    void Should_refresh_lock_expiry() {
        String lockId = "lockId";

        couchbaseChangelogLocker.refreshLockExpiry(lockId);

        verify(collection).touch(eq(lockId), any());
    }

    @Test
    void Should_catch_exception_on_refresh_lock_expiry() {
        String lockId = "lockId";
        when(collection.touch(eq(lockId), any())).thenThrow(new RuntimeException("Mocked"));

        assertThatCode(() -> couchbaseChangelogLocker.refreshLockExpiry(lockId)).doesNotThrowAnyException();
    }

    @Test
    void Should_try_acquire() {
        String bucketName = "bucketName";
        String owner = "owner";
        Instant lockedAt = Instant.now();

        boolean result = couchbaseChangelogLocker.tryAcquire(bucketName, owner, lockedAt);
        assertThat(result).isTrue();

        verify(collection).insert(eq(bucketName), any(), any());
    }

    @Test
    void Should_catch_exception_on_try_acquire() {
        String bucketName = "bucketName";
        String owner = "owner";
        Instant lockedAt = Instant.now();

        when(collection.insert(any(), any(), any())).thenThrow(new DocumentExistsException(mock(ErrorContext.class)));

        boolean result = couchbaseChangelogLocker.tryAcquire(bucketName, owner, lockedAt);
        assertThat(result).isFalse();

        verify(collection).insert(eq(bucketName), any(), any());
    }

    @Test
    void Should_return_free_lock_status() {
        String lockId = "lockId";
        String owner = "owner";

        configureFreeLockStatus(lockId);

        LockStatus result = couchbaseChangelogLocker.getLockStatus(lockId, owner);
        assertThat(result).isEqualTo(LockStatus.FREE);

        verify(collection).get(lockId);
    }

    @Test
    void Should_return_owner_lock_status() {
        String lockId = "lockId";
        String owner = "owner";

        configureOwnerLockStatus(lockId, owner);

        LockStatus result = couchbaseChangelogLocker.getLockStatus(lockId, owner);
        assertThat(result).isEqualTo(LockStatus.OWNER);

        verify(collection).get(lockId);
    }

    @Test
    void Should_return_not_owner_lock_status() {
        String lockId = "lockId";
        String owner = "owner";

        configureNotOwnerLockStatus(lockId);

        LockStatus result = couchbaseChangelogLocker.getLockStatus(lockId, owner);
        assertThat(result).isEqualTo(LockStatus.NOT_OWNER);

        verify(collection).get(lockId);
    }

    @Test
    void Should_release_if_owned() {
        String lockId = "lockId";
        String owner = "owner";

        configureOwnerLockStatus(lockId, owner);

        assertThatCode(() -> couchbaseChangelogLocker.release(lockId, owner)).doesNotThrowAnyException();

        verify(collection).get(lockId);
        verify(collection).remove(lockId);
    }

    @Test
    void Should_throw_exception_if_owned_by_another() {
        String lockId = "lockId";
        String owner = "owner";

        configureNotOwnerLockStatus(lockId);

        assertThatExceptionOfType(LockException.class)
                .isThrownBy(() -> couchbaseChangelogLocker.release(lockId, owner))
                .withMessage("Service [%s] is not an owner of this lock ([%s])", owner, lockId);

        verify(collection).get(lockId);
        verify(collection, never()).remove(any());
    }

    @Test
    void Should_not_do_anything_if_free() {
        String lockId = "lockId";
        String owner = "owner";

        configureFreeLockStatus(lockId);

        assertThatCode(() -> couchbaseChangelogLocker.release(lockId, owner)).doesNotThrowAnyException();

        verify(collection).get(lockId);
        verify(collection, never()).remove(any());
    }

    private void configureOwnerLockStatus(String lockId, String owner) {
        GetResult getResult = mock(GetResult.class);
        CouchbaseLock couchbaseLock = mock(CouchbaseLock.class);
        when(collection.get(lockId)).thenReturn(getResult);
        when(getResult.contentAs(CouchbaseLock.class)).thenReturn(couchbaseLock);
        when(couchbaseLock.getOwner()).thenReturn(owner);
    }

    private void configureNotOwnerLockStatus(String lockId) {
        GetResult getResult = mock(GetResult.class);
        CouchbaseLock couchbaseLock = mock(CouchbaseLock.class);
        when(collection.get(lockId)).thenReturn(getResult);
        when(getResult.contentAs(CouchbaseLock.class)).thenReturn(couchbaseLock);
        when(couchbaseLock.getOwner()).thenReturn("");
    }

    private void configureFreeLockStatus(String lockId) {
        when(collection.get(lockId)).thenThrow(new CouchbaseException("mocked"));
    }
}
