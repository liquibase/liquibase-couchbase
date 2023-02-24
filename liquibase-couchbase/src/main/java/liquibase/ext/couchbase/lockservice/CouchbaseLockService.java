package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.java.Collection;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.LockService;
import liquibase.logging.Logger;
import lombok.Setter;

import java.time.Instant;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.Instant.now;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

public class CouchbaseLockService implements LockService {

    public static String LOCK_COLLECTION_NAME = "CHANGELOGLOCKS";

    private CouchbaseLiquibaseDatabase database;
    private final Logger logger = Scope.getCurrentScope()
            .getLog(getClass());
    private CouchbaseChangelogLocker locker;

    @Setter
    private ServiceProvider serviceProvider;

    private boolean isInitialized = false;
    @Setter
    private String bucketName;
    private final AtomicBoolean hasChangeLogLock = new AtomicBoolean(false);
    private final Timer timer = new Timer("CouchbaseLockService Timer", true);
    private final TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            updateLockState();
        }
    };

    /**
     * Time to wait for the lock to be acquired, in milliseconds. Default value is 5 minutes.
     */

    @Setter(onMethod = @__( {@Override}))
    private long changeLogLockWaitTime = TimeUnit.MINUTES.toMillis(5);

    /**
     * Time to wait between rechecking the lock, in milliseconds. Default value is 10 seconds.
     */

    @Setter(onMethod = @__( {@Override}))
    private long changeLogLockRecheckTime = TimeUnit.SECONDS.toMillis(10);

    @Override
    public void setDatabase(Database database) {
        this.database = (CouchbaseLiquibaseDatabase) database;
    }

    @Override
    public int getPriority() {
        return PRIORITY_SPECIALIZED;
    }

    @Override
    public boolean supports(Database database) {
        return database instanceof CouchbaseLiquibaseDatabase;
    }

    @Override
    public boolean hasChangeLogLock() {
        return hasChangeLogLock.get();
    }

    @Override
    public void init() {
        if (isInitialized) { return; }

        logger.info("Initializing CouchbaseLockService");

        serviceProvider = Optional.ofNullable(serviceProvider)
                .orElseGet(() -> new ContextServiceProvider(database));
        Collection collection = serviceProvider.getServiceCollection(LOCK_COLLECTION_NAME);
        locker = new CouchbaseChangelogLocker(collection);
        bucketName = collection.bucketName();
        logger.info("Using locks for bucket " + bucketName);

        updateLockState();
        timer.schedule(timerTask, 0, changeLogLockRecheckTime);

        isInitialized = true;
    }

    private void updateLockState() {
        hasChangeLogLock.set(locker.lockAcquiredOn(bucketName));
    }

    @Override
    public void waitForLock() throws LockException {
        init();

        if (acquireLock()) { return; }

        Instant timeToGiveUp = now().plusMillis(changeLogLockWaitTime);
        while (!acquireLock()) {
            failIfTimeLimitExceeded(timeToGiveUp);

            logger.info("Waiting for lock");
            doWait();
        }
    }

    private static void failIfTimeLimitExceeded(Instant timeToGiveUp) throws LockException {
        if (now().isAfter(timeToGiveUp)) {
            throw new LockException("Could not acquire lock");
        }
    }

    private void doWait() throws LockException {
        try {
            TimeUnit.MILLISECONDS.sleep(changeLogLockRecheckTime);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
    }

    @Override
    public synchronized boolean acquireLock() throws LockException {
        init();

        if (hasChangeLogLock()) { return false; }

        try {
            logger.info("Acquiring lock on bucket " + bucketName);
            Instant lockedAt = now();
            locker.lock(bucketName, lockedAt);
            hasChangeLogLock.set(true);
        } catch (Exception e) {
            throw new LockException(e);
        }
        return true;
    }

    @Override
    public synchronized void releaseLock() throws LockException {
        logger.info("Releasing lock on bucket " + bucketName);
        try {
            locker.release(bucketName);
            hasChangeLogLock.set(false);
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

    @Override
    public void forceReleaseLock() throws LockException {
        try {
            locker.forceRelease();
            hasChangeLogLock.set(false);
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

    @Override
    public DatabaseChangeLogLock[] listLocks() {
        return new DatabaseChangeLogLock[0];
    }

    @Override
    public void destroy() {
        logger.info("Destroying CouchbaseLockService");
        try {
            releaseLock();
        } catch (LockException e) {
            logger.severe("Could not release lock during destroy");
        }
    }

    @Override
    public void reset() {
        logger.info("Resetting CouchbaseLockService");
        isInitialized = false;
        timer.cancel();
        try {
            if (hasChangeLogLock()) {
                releaseLock();
            }
        } catch (LockException e) {
            logger.severe("Could not release lock during reset");
        }
    }

}
