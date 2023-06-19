package liquibase.ext.couchbase.changelog;

import liquibase.Scope;
import liquibase.changelog.AbstractChangeLogHistoryService;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.exception.DatabaseException;
import liquibase.exception.DatabaseHistoryException;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.operator.ChangeLogOperator;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;
import liquibase.ext.couchbase.types.Keyspace;
import liquibase.logging.Logger;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * Abstract class for all NoSQL history services, extends {@link AbstractChangeLogHistoryService}<br><br> {@link CouchbaseHistoryService} is
 * the default implementation for Couchbase
 * @see ChangeLogOperator
 * @see RanChangeSet
 */

@Getter
@Setter
public abstract class NoSqlHistoryService extends AbstractChangeLogHistoryService {

    private static final String CREATING_TEMPLATE = "Creating history collection with name: %s";
    private static final String CREATED_TEMPLATE = "Created history collection: %s";
    private Integer lastChangeLogOrder;
    private List<RanChangeSet> ranChangeSetList;
    private ChangeLogOperator changeLogOperator;
    private ServiceProvider serviceProvider;
    private boolean initialized;
    private final Logger log = Scope.getCurrentScope().getLog(getClass());

    @Override
    public CouchbaseLiquibaseDatabase getDatabase() {
        return (CouchbaseLiquibaseDatabase) super.getDatabase();
    }

    @Override
    public void setDatabase(Database database) {
        CouchbaseLiquibaseDatabase couchbaseDatabase = (CouchbaseLiquibaseDatabase) database;
        super.setDatabase(couchbaseDatabase);
        // TODO think how we can init operators (harness test doesn't see database in scope)
        changeLogOperator = new ChangeLogOperator(couchbaseDatabase);
        serviceProvider = new ContextServiceProvider(couchbaseDatabase);
    }

    /**
     * If there is no table in the database for recording change history create one.
     */
    @Override
    public void init() {
        if (initialized) {
            return;
        }
        if (!existsChangeLogCollection()) {
            log.info("Create Change Log Collection");

            Keyspace keyspace = keyspace(SERVICE_BUCKET_NAME, DEFAULT_SERVICE_SCOPE, CHANGE_LOG_COLLECTION);
            log.info(format(CREATING_TEMPLATE, keyspace.getFullPath()));
            createRepository();
            log.info(format(CREATED_TEMPLATE, keyspace.getFullPath()));
        }
        initialized = true;
    }

    @Override
    public void setExecType(final ChangeSet changeSet, final ChangeSet.ExecType execType) throws DatabaseException {
        markChangeSetRun(changeSet, execType);
        if (ranChangeSetList != null) {
            ranChangeSetList.add(new RanChangeSet(changeSet, execType, null, null));
        }
    }

    /**
     * Returns the ChangeSets that have been run against the current getDatabase().
     */
    @Override
    public List<RanChangeSet> getRanChangeSets() throws DatabaseException {
        // liquibase invokes it several times, but we only need to get list 1 time.
        if (isNull(ranChangeSetList)) {
            ranChangeSetList = getAllChangeLogs();
        }
        return unmodifiableList(ranChangeSetList);
    }

    @Override
    public RanChangeSet getRanChangeSet(final ChangeSet changeSet) throws DatabaseException, DatabaseHistoryException {
        if (!existsChangeLogCollection()) {
            return null;
        }
        return super.getRanChangeSet(changeSet);
    }

    @Override
    public void tag(final String tagString) {
        changeLogOperator.tagLastChangeSet(tagString);
    }

    @Override
    public void removeFromHistory(final ChangeSet changeSet) {
        changeLogOperator.removeChangeSetFromHistory(changeSet);

        if (isEmpty(ranChangeSetList)) {
            return;
        }
        ranChangeSetList.remove(new RanChangeSet(changeSet));
    }

    @Override
    public void replaceChecksum(final ChangeSet changeSet) {
        // TODO implement
    }

    @Override
    public void clearAllCheckSums() {
        // TODO implement
    }

    @Override
    public boolean tagExists(final String tag) {
        // TODO implement
        return true;
    }

    @Override
    public void destroy() {
        // TODO implement
    }

    protected abstract void createRepository();

    protected abstract boolean existsChangeLogCollection();

    protected abstract void markChangeSetRun(ChangeSet changeSet, ChangeSet.ExecType execType) throws DatabaseException;

    protected abstract List<RanChangeSet> getAllChangeLogs() throws DatabaseException;

}
