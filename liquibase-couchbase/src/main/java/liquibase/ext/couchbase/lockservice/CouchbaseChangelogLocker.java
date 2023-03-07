package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.core.error.DocumentExistsException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.kv.InsertOptions;

import java.time.Duration;
import java.time.Instant;

import liquibase.Scope;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.RequiredArgsConstructor;
import static com.couchbase.client.java.kv.InsertOptions.insertOptions;
import static java.lang.String.format;

/**
 * Collection based locker for {@link com.couchbase.client.java.Bucket Bucket}
 * Operates with documents in Liquibase service {@link Keyspace }
 *
 * @see CouchbaseLock
 * @see com.couchbase.client.core.service.ServiceScope
 */
@RequiredArgsConstructor
public class CouchbaseChangelogLocker {
    private final Logger log = Scope.getCurrentScope().getLog(CouchbaseChangelogLocker.class);

    private final Collection collection;
    private final Duration expiry = Duration.ofMinutes(3L);
    private final Duration expiryProlongation = Duration.ofMinutes(1L);

    /**
     * Trying to acquire lock, attempt is success only when there is no such document with given bucketName.
     * Inserts ${@link CouchbaseLock} in success.
     *
     * @param bucketName specific bucket name which we try to lock
     * @param owner      unique liquibase app service id
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
     *
     * @param lockId specific bucket name which we try to lock
     * @param owner  unique liquibase app service id
     * @throws LockException if it doesn't have ownership
     */
    public void release(String lockId, String owner) throws LockException {
        if (!isHeldBy(lockId, owner)) {
            throw new LockException(format("Service [%s] is not an owner of this lock ([%s])", owner, lockId));
        }

        collection.remove(lockId);
    }

    /**
     * Checking whether it holds specific lock or not
     *
     * @param lockId specific bucket name which we try to lock
     * @param owner  unique liquibase app service id
     */
    public boolean isHeldBy(String lockId, String owner) {
        try {
            String currentOwner = collection.get(lockId)
                    .contentAs(CouchbaseLock.class)
                    .getOwner();
            return currentOwner.equals(owner);
        } catch (CouchbaseException e) {
            return false;
        }
    }

    /**
     * Releasing lock without any checks and ownership
     *
     * @param lockId specific bucket name which we are trying to release
     */
    public void forceRelease(String lockId) {
        try {
            collection.remove(lockId);
        } catch (Exception e) {
            log.severe(format("Could not force release lock for %s , because: %s", lockId, e.getMessage()));
        }
    }

    /**
     * Add TTL to existing lock, maybe useful on long changesets
     *
     * @param lockId specific bucket name which we prolong
     */
    public void refreshLockExpiry(String lockId) {
        try {
            collection.touch(lockId, expiryProlongation);
        } catch (Exception e) {
            log.severe(format("Could not prolong expiry lock for %s , because: %s", lockId, e.getMessage()));
        }
    }

    private boolean doTryAcquire(String bucketName, String owner, Instant lockedAt) {
        CouchbaseLock lock = new CouchbaseLock(bucketName, owner, lockedAt.toEpochMilli());
        InsertOptions options = insertOptions().expiry(expiry);
        collection.insert(bucketName, lock, options);
        return true;
    }

}
