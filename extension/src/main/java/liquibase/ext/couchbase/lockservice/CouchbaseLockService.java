package liquibase.ext.couchbase.lockservice;

import liquibase.Scope;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.LockException;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.LockService;
import liquibase.logging.Logger;

import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

public class CouchbaseLockService implements LockService {

	private final Logger logger = Scope.getCurrentScope().getLog(getClass());

	private Database database;

	@Override
	public void setDatabase(Database database) {
		this.database = database;
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
		return false;
	}

	@Override
	public void waitForLock() throws LockException {}

	@Override
	public boolean acquireLock() throws LockException {
		return false;
	}

	@Override
	public void releaseLock() throws LockException { }

	@Override
	public void forceReleaseLock() throws LockException { }

	@Override
	public DatabaseChangeLogLock[] listLocks() {
		return new DatabaseChangeLogLock[0];
	}

	@Override
	public void init() throws DatabaseException {
		logger.info("Initializing CouchbaseLockService");
	}

	@Override
	public void destroy() throws DatabaseException {
		logger.info("Destroying CouchbaseLockService");
	}

	@Override
	public void reset() {
		logger.info("Resetting CouchbaseLockService");
	}

	@Override
	public void setChangeLogLockWaitTime(long changeLogLockWaitTime) {}

	@Override
	public void setChangeLogLockRecheckTime(long changeLogLockRecheckTime) {}

}
