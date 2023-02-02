package liquibase.ext.lockservice;

import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.LockException;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.lockservice.DatabaseChangeLogLock;
import liquibase.lockservice.LockService;

import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

public class CouchbaseLockService implements LockService {

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
	public void waitForLock() throws LockException {

	}

	@Override
	public boolean acquireLock() throws LockException {
		return false;
	}

	@Override
	public void releaseLock() throws LockException {

	}

	@Override
	public void forceReleaseLock() throws LockException {

	}

	@Override
	public DatabaseChangeLogLock[] listLocks() {
		return new DatabaseChangeLogLock[0];
	}

	@Override
	public void init() throws DatabaseException {

	}

	@Override
	public void destroy() throws DatabaseException {

	}

	@Override
	public void reset() {

	}

	@Override
	public void setChangeLogLockWaitTime(long changeLogLockWaitTime) {

	}

	@Override
	public void setChangeLogLockRecheckTime(long changeLogLockRecheckTime) {

	}

}
