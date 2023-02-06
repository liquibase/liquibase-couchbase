package liquibase.ext.changelog;

import liquibase.Scope;
import liquibase.changelog.AbstractChangeLogHistoryService;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.exception.DatabaseException;
import liquibase.exception.DatabaseHistoryException;
import liquibase.executor.ExecutorService;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.executor.NoSqlExecutor;
import lombok.Getter;
import lombok.Setter;

import java.time.Clock;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static java.lang.Boolean.FALSE;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

public abstract class NoSqlHistoryService extends AbstractChangeLogHistoryService {
    @Getter
    private List<RanChangeSet> ranChangeSetList;
    private boolean serviceInitialized;
    @Getter
    private Boolean hasDatabaseChangeLogTable;
    @Getter
    private Boolean adjustedChangeLogTable = FALSE;
    @Getter
    @Setter
    private Clock clock = Clock.systemDefaultZone();

    public int getPriority() {
        return PRIORITY_SPECIALIZED;
    }

    public String getDatabaseChangeLogTableName() {
        return getDatabase().getDatabaseChangeLogTableName();
    }

    public boolean canCreateChangeLogTable() {
        return true;
    }

    public boolean isServiceInitialized() {
        return serviceInitialized;
    }

    @SuppressWarnings("unchecked")
    public CouchbaseLiquibaseDatabase getNoSqlDatabase() {
        return (CouchbaseLiquibaseDatabase) getDatabase();
    }

    public NoSqlExecutor getExecutor() {
        return (NoSqlExecutor) Scope.getCurrentScope().getSingleton(ExecutorService.class).getExecutor(NoSqlExecutor.EXECUTOR_NAME, getDatabase());
    }

    @Override
    public void reset() {
        super.reset();
        this.ranChangeSetList = null;
        this.serviceInitialized = false;
        this.hasDatabaseChangeLogTable = null;
        this.adjustedChangeLogTable = FALSE;
    }

    @Override
    public void init() throws DatabaseException {
        //TODO implement
    }

    public boolean hasDatabaseChangeLogTable() {
        //TODO implement
        return true;
    }

    @Override
    public List<RanChangeSet> getRanChangeSets() throws DatabaseException {
        //TODO implement
        return Collections.emptyList();
    }

    @Override
    public void replaceChecksum(final ChangeSet changeSet) throws DatabaseException {
        //TODO implement
    }

    @Override
    public RanChangeSet getRanChangeSet(final ChangeSet changeSet) throws DatabaseException, DatabaseHistoryException {
        return super.getRanChangeSet(changeSet);
    }

    @Override
    public void setExecType(final ChangeSet changeSet, final ChangeSet.ExecType execType) throws DatabaseException {
        //TODO implement
    }

    @Override
    public void removeFromHistory(final ChangeSet changeSet) throws DatabaseException {
        //TODO implement
    }

    @Override
    public int getNextSequenceValue() throws DatabaseException {
        //TODO implement
        return new Random().nextInt(100);
    }

    @Override
    public void tag(final String tagString) throws DatabaseException {
        //TODO implement
    }

    @Override
    public boolean tagExists(final String tag) throws DatabaseException {
        //TODO implement
        return true;
    }

    @Override
    public void clearAllCheckSums() throws DatabaseException {
        //TODO implement
    }

    @Override
    public void destroy() {
        //TODO implement
    }

}
