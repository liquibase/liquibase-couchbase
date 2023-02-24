package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.Collection;

import java.time.Instant;

import liquibase.exception.LockException;
import lombok.RequiredArgsConstructor;
import static java.lang.String.format;

@RequiredArgsConstructor
public class CouchbaseChangelogLocker {
    private final Collection collection;

    /**
     * Trying acquire
     * @return attempt result
     */
    public boolean tryAcquire(String bucketName, String owner, Instant lockedAt) {
        try {
            return doTryAcquire(bucketName, owner, lockedAt);
        } catch (DocumentExistsException documentExistsException) {
            return false;
        }
    }

    /**
     * Trying release lock
     * @throws LockException if it doesn't have ownership
     */
    public void release(String lockId, String owner) throws LockException {
        if (!isHeldBy(lockId, owner)) {
            throw new LockException(format("Service [%s] is not owner of [%s] lock", owner, lockId));
        }

        collection.remove(lockId);
    }

    /**
     * Checking whether it holds specific lock or not
     */
    public boolean isHeldBy(String lockId, String owner) {
        try {
            return collection.get(lockId)
                    .contentAs(CouchbaseLock.class)
                    .getOwner().equals(owner);
        } catch (CouchbaseException e) {
            return false;
        }
    }

    /**
     * Releasing lock without any checks and ownership
     */
    public void forceRelease(String lockId) {
        try {
            collection.remove(lockId);
        } catch (Exception ignored) {

        }
    }

    private boolean doTryAcquire(String bucketName, String owner, Instant lockedAt) {
        CouchbaseLock lock = new CouchbaseLock(bucketName, owner, lockedAt.toEpochMilli());
        collection.insert(bucketName, lock);
        return true;
    }

}
