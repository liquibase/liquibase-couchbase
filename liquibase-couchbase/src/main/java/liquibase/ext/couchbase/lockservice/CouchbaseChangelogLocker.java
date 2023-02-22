package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.java.Collection;
import liquibase.Scope;
import liquibase.logging.Logger;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

import java.time.Instant;

@RequiredArgsConstructor
public class CouchbaseChangelogLocker {

    private final Collection collection;
    private final Logger logger = Scope.getCurrentScope().getLog(getClass());

    public void lock(String lockId, Instant lockedAt) {
        collection.insert(lockId, new CouchbaseLock(lockId, lockedAt.toEpochMilli()));
    }

    public void release(String lockId) {
        try {
            collection.remove(lockId);
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }
    }

    public void forceRelease() {
        // TODO implement method
        throw new NotImplementedException("forceRelease");
    }

    public boolean lockAcquiredOn(String lockId) {
        return collection.exists(lockId).exists();
    }

}
