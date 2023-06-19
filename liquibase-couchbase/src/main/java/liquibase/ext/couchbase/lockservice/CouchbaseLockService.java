package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.java.Collection;
import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.LockService;
import liquibase.logging.Logger;
import liquibase.servicelocator.LiquibaseService;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static liquibase.ext.couchbase.configuration.CouchbaseLiquibaseConfiguration.CHANGELOG_LOCK_COLLECTION_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

@LiquibaseService
public class CouchbaseLockService implements LockService {

    private final Logger logger = Scope.getCurrentScope()
            .getLog(getClass());

    @Getter
    private final String serviceId;

    private boolean isInitialized = false;

    private CouchbaseChangelogLocker locker;
    private CouchbaseLiquibaseDatabase database;

    @Setter
    private String bucketName;

    private final AtomicBoolean hasLock = new AtomicBoolean(false);
    @Setter
    private ServiceProvider serviceProvider;
    private final Timer timer = new Timer("CouchbaseLockService Timer", true);
    private final TimerTask refreshLockStateTask = new TimerTask() {
        @Override
        public void run() {
            refreshLockExpiry();
        }
    };

    /**
     * Time to wait for the lock to be acquired, in milliseconds. Default value is 300 seconds.
     */
    @Setter(onMethod = @__( {@Override}))
    private long changeLogLockWaitTime = CouchbaseLiquibaseConfiguration.getChangelogWaitTime().toMillis();

    /**
     * Time to wait between rechecking the lock, in milliseconds. Default value is 10 seconds.
     */
    @Setter(onMethod = @__( {@Override}))
    private long changeLogLockRecheckTime = CouchbaseLiquibaseConfiguration.getChangelogRecheckTime().toMillis();

    public CouchbaseLockService(String serviceId) {
        this.serviceId = serviceId;
    }

    public CouchbaseLockService() {
        this(UUID.randomUUID().toString());
    }

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
        return hasLock.get();
    }

    @Override
    public void init() {
        if (isInitialized) {
            return;
        }

        logger.info(format("Initializing CouchbaseLockService [%s]", serviceId));

        serviceProvider = ofNullable(serviceProvider)
                .orElse(new ContextServiceProvider(database));
        Collection collection = serviceProvider.getServiceCollection(CHANGELOG_LOCK_COLLECTION_NAME.getCurrentValue());
        locker = new CouchbaseChangelogLocker(collection);
        bucketName = collection.bucketName();
        logger.info(format("Using locks for bucket [%s]", bucketName));

        refreshLockExpiry();
        timer.schedule(refreshLockStateTask, 0, changeLogLockRecheckTime);

        isInitialized = true;
    }

    @Override
    public void waitForLock() throws LockException {
        init();

        if (hasChangeLogLock()) {
            return;
        }

        Instant timeToGiveUp = now().plusMillis(changeLogLockWaitTime);
        while (!acquireLock()) {
            failIfTimeLimitExceeded(timeToGiveUp);

            logger.info(format("Service [%s] is waiting for a lock on the bucket [%s]", serviceId, bucketName));
            doWait();
        }
    }

    @Override
    public boolean acquireLock() {
        init();

        if (hasChangeLogLock()) {
            return true;
        }

        Instant lockedAt = now();
        logger.info(format("Acquiring lock on the bucket [%s] from the service [%s]", bucketName, serviceId));
        boolean locked = locker.tryAcquire(bucketName, serviceId, lockedAt);
        hasLock.set(locked);
        String result = locked ? "has been successfully acquired" : "hasn't been acquired";
        logger.info(format("Lock on the bucket [%s] from the service [%s] %s", bucketName, serviceId, result));

        return hasLock.get();
    }

    @Override
    public void releaseLock() throws LockException {
        if (isInitialized) {
            logger.info(format("Releasing lock on the bucket [%s] from the service [%s]", bucketName, serviceId));
            locker.release(bucketName, serviceId);
            hasLock.set(false);
        }
    }

    @Override
    public void forceReleaseLock() throws LockException {
        if (isInitialized) {
            try {
                cleanTimer();
                locker.forceRelease(bucketName);
                hasLock.set(false);
            } catch (Exception e) {
                throw new LockException(e);
            }
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
        } catch (Exception e) {
            logger.severe("Could not release a lock during the destroy");
        }
    }

    @Override
    public void reset() {
        logger.info("Resetting CouchbaseLockService");
        try {
            cleanTimer();
            if (hasChangeLogLock()) {
                releaseLock();
            }
        } catch (Exception e) {
            logger.severe("Could not release a lock during the reset");
        }
        isInitialized = false;
    }

    private void cleanTimer() {
        timer.cancel();
        timer.purge();
        refreshLockStateTask.cancel();
    }

    private void refreshLockExpiry() {
        if (hasLock.get()) {
            locker.refreshLockExpiry(bucketName);
        }
    }

    private void failIfTimeLimitExceeded(Instant timeToGiveUp) throws LockException {
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

}
