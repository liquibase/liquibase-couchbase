package liquibase.ext.couchbase.changelog;

import liquibase.change.core.TagDatabaseChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.database.Database;
import liquibase.ext.couchbase.mapper.ChangeSetMapper;
import liquibase.ext.couchbase.statement.CollectionExistsStatement;
import liquibase.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import static liquibase.ext.couchbase.database.Constants.COUCHBASE_PRODUCT_NAME;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;
import static liquibase.plugin.Plugin.PRIORITY_SPECIALIZED;

/**
 * Concrete implementation of {@link NoSqlHistoryService} for Couchbase
 */

public class CouchbaseHistoryService extends NoSqlHistoryService {

    @Override
    public int getPriority() {
        return PRIORITY_SPECIALIZED;
    }

    @Override
    protected void createRepository() {
        getChangeLogOperator().createChangeLogCollection();
    }

    @Override
    protected boolean existsChangeLogCollection() {
        String bucketName = SERVICE_BUCKET_NAME;

        // TODO think about moving it to bucketOperator, but without providing bucket, because bucket may not exist
        CollectionExistsStatement collectionExistsStatement =
                new CollectionExistsStatement(bucketName, DEFAULT_SERVICE_SCOPE, CHANGE_LOG_COLLECTION);
        return collectionExistsStatement.isTrue(getDatabase().getConnection());
    }

    @Override
    public boolean supports(final Database database) {
        return COUCHBASE_PRODUCT_NAME.equals(database.getDatabaseProductName());
    }

    protected void markChangeSetRun(final ChangeSet changeSet, final ChangeSet.ExecType execType) {
        final String tag = extractTag(changeSet);
        int nextSequenceValue = getNextSequenceValue();
        String deploymentId = getDeploymentId();

        if (execType.ranBefore) {
            return;
        }
        CouchbaseChangeLog changeLog = ChangeSetMapper.mapToCouchbaseChangeLog(changeSet);
        changeLog.setTag(tag);
        changeLog.setOrderExecuted(nextSequenceValue);
        changeLog.setDeploymentId(deploymentId);
        changeLog.setExecType(execType);

        getChangeLogOperator().insertChangeLog(changeLog);
    }

    private String extractTag(final ChangeSet changeSet) {
        return changeSet.getChanges().stream()
                .filter(TagDatabaseChange.class::isInstance)
                .map(change -> {
                    TagDatabaseChange tagDatabaseChange = (TagDatabaseChange) change;
                    return StringUtil.trimToNull(tagDatabaseChange.getTag());
                })
                .findFirst()
                .orElse(null);
    }

    @Override
    public int getNextSequenceValue() {
        if (getLastChangeLogOrder() != null) {
            int nextValue = getLastChangeLogOrder() + 1;
            setLastChangeLogOrder(nextValue);
            return nextValue;
        }

        int lastOrderExecuted = getChangeLogOperator().findLastOrderExecuted();
        int nextValue = lastOrderExecuted + 1;

        setLastChangeLogOrder(nextValue);
        return nextValue;
    }

    @Override
    protected List<RanChangeSet> getAllChangeLogs() {
        if (existsChangeLogCollection()) {
            return getChangeLogOperator().getAllChangeLogs();
        }
        return new ArrayList<>();
    }

}
