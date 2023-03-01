package liquibase.ext.couchbase.lockservice;

import com.couchbase.client.java.Collection;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.LockService;
import liquibase.logging.Logger;
import liquibase.servicelocator.LiquibaseService;
import lombok.Getter;
import lombok.Setter;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.util.Optional.ofNullable;
import static liquibase.ext.couchbase.provider.LiquibasePropertyProvider.getPropertyOrDefault;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

@LiquibaseService
public class CouchbaseLockService implements LockService {

    private static final int WAIT_LOCK_TIME = parseInt(getPropertyOrDefault("waitLockTimeInSec", "300"));
    private static final int WAIT_RECHECK_LOCK_TIME = parseInt(getPropertyOrDefault("waitRecheckLockTimeInSec", "10"));
    public static final String LOCK_COLLECTION_NAME = "CHANGELOGLOCKS";
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
            refreshLockState();
        }
    };

    /**
     * Time to wait for the lock to be acquired, in milliseconds. Default value is 300 seconds.
     */
    @Setter(onMethod = @__({@Override}))
    private long changeLogLockWaitTime = TimeUnit.SECONDS.toMillis(WAIT_LOCK_TIME);

    /**
     * Time to wait between rechecking the lock, in milliseconds. Default value is 10 seconds.
     */
    @Setter(onMethod = @__({@Override}))
    private long changeLogLockRecheckTime = TimeUnit.SECONDS.toMillis(WAIT_RECHECK_LOCK_TIME);

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

        logger.info("Initializing CouchbaseLockService");

        serviceProvider = ofNullable(serviceProvider)
                .orElse(new ContextServiceProvider(database));
        Collection collection = serviceProvider.getServiceCollection(LOCK_COLLECTION_NAME);
        locker = new CouchbaseChangelogLocker(collection);
        bucketName = collection.bucketName();
        logger.info("Using locks for bucket " + bucketName);

        refreshLockState();
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

            logger.info(format("Service [%s] is waiting for lock on bucket [%s]", serviceId, bucketName));
            doWait();
        }
    }

    private void refreshLockState() {
        hasLock.set(locker.isHeldBy(bucketName, serviceId));
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

    @Override
    public boolean acquireLock() {
        init();

        if (hasChangeLogLock()) {
            return true;
        }

        Instant lockedAt = now();
        logger.info(format("Acquiring lock on bucket [%s], serviceId [%s]", bucketName, serviceId));
        boolean locked = locker.tryAcquire(bucketName, serviceId, lockedAt);
        hasLock.set(locked);
        String result = locked ? "has been successfully received" : "has been not received";
        logger.info(format("Lock on bucket [%s], with serviceId [%s] ,%s", bucketName, serviceId, result));

        return hasLock.get();
    }

    @Override
    public void releaseLock() throws LockException {
        logger.info(format("Releasing lock on bucket [%s] , serviceID [%s]", bucketName, serviceId));
        locker.release(bucketName, serviceId);
        hasLock.set(false);
        logger.info(format("Lock on bucket [%s] ,with serviceID [%s], has been released successfully", bucketName, serviceId));
    }

    @Override
    public void forceReleaseLock() throws LockException {
        try {
            locker.forceRelease(bucketName);
            hasLock.set(false);
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

    @Override
    public DatabaseChangeLogLock[] listLocks() {
        return new DatabaseChangeLogLock[0];
    }

    @Override
    @PreDestroy
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
