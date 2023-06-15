package liquibase.ext.couchbase.operator;

import com.couchbase.client.core.deps.com.google.common.annotations.VisibleForTesting;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.mapper.ChangeSetMapper;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;

import java.util.List;

import static com.couchbase.client.java.kv.MutateInSpec.upsert;
import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

/**
 * Will move to a separate module in the future. Right now it provides required functionality for the history service.
 */

public class ChangeLogOperator {

    public static final String SELECT_ALL_CHANGELOGS_N1QL = "SELECT DATABASECHANGELOG.* FROM DATABASECHANGELOG " +
            "ORDER BY orderExecuted ASC";
    public static final String SELECT_LAST_ORDER_N1QL = "SELECT orderExecuted FROM DATABASECHANGELOG " +
            "ORDER BY orderExecuted DESC LIMIT 1";

    public static final String SELECT_LAST_CHANGE_SET_ID_N1QL = "SELECT meta().id FROM DATABASECHANGELOG " +
            "ORDER BY orderExecuted DESC LIMIT 1";

    private static final int NO_ORDER = 0;

    private final CouchbaseLiquibaseDatabase database;
    private final ServiceProvider serviceProvider;

    public ChangeLogOperator(CouchbaseLiquibaseDatabase database) {
        this(database, new ContextServiceProvider(database));
    }

    @VisibleForTesting
    ChangeLogOperator(CouchbaseLiquibaseDatabase database, ServiceProvider serviceProvider) {
        this.database = database;
        this.serviceProvider = serviceProvider;
    }

    public void createChangeLogCollection() {
        Scope scope = serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION);
        CreatePrimaryQueryIndexOptions indexOptions = createPrimaryQueryIndexOptions()
                .scopeName(scope.name())
                .collectionName(CHANGE_LOG_COLLECTION)
                .ignoreIfExists(true)
                .indexName("liquibase-changelog-primary-index");
        database.getConnection().getCluster().queryIndexes().createPrimaryIndex(scope.bucketName(), indexOptions);
    }

    public void insertChangeLog(CouchbaseChangeLog changeLog) {
        String id = generateId(changeLog.getFileName(), changeLog.getId(), changeLog.getAuthor());

        Collection collection = serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION);
        collection.insert(id, changeLog);
    }

    public int findLastOrderExecuted() {
        Scope scope = serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION);

        QueryOptions queryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        List<JsonObject> rows = scope.query(SELECT_LAST_ORDER_N1QL, queryOptions).rowsAsObject();

        return rows.stream()
                .map(jsonObject -> jsonObject.getInt("orderExecuted"))
                .findFirst()
                .orElse(NO_ORDER);
    }

    public List<RanChangeSet> getAllChangeLogs() {
        Scope scope = serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION);
        QueryOptions queryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);

        return scope.query(SELECT_ALL_CHANGELOGS_N1QL, queryOptions)
                .rowsAs(CouchbaseChangeLog.class)
                .stream()
                .map(ChangeSetMapper::mapToRanChangeSet)
                .collect(toList());
    }

    public void removeChangeSetFromHistory(ChangeSet changeSet) {
        String id = generateId(changeSet.getFilePath(), changeSet.getId(), changeSet.getAuthor());
        Collection collection = serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION);
        collection.remove(id);
    }

    private String generateId(String filePath, String changeSetId, String author) {
        // TODO look on key generator in future
        return format("%s::%s::%s", filePath, changeSetId, author);
    }

    public void tagLastChangeSet(String tagString) {
        Scope scope = serviceProvider.getScopeOfCollection(CHANGE_LOG_COLLECTION);
        Collection collection = serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION);

        QueryOptions queryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        List<JsonObject> rows = scope.query(SELECT_LAST_CHANGE_SET_ID_N1QL, queryOptions).rowsAsObject();

        if (isEmpty(rows)) {
            return;
        }
        String changeSetId = rows.get(0).getString("id");
        collection.mutateIn(changeSetId, singletonList(upsert("tag", tagString)));
    }
}
