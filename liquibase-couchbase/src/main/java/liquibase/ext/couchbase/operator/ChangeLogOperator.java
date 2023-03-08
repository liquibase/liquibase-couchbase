package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import liquibase.changelog.RanChangeSet;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import liquibase.ext.couchbase.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.couchbase.mapper.ChangeSetMapper;
import liquibase.ext.couchbase.provider.ContextServiceProvider;
import liquibase.ext.couchbase.provider.ServiceProvider;

import java.util.List;
import java.util.stream.Collectors;

import static com.couchbase.client.java.manager.query.CreatePrimaryQueryIndexOptions.createPrimaryQueryIndexOptions;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;

/**
 * Will move to a separate module in the future. Right now it provides required functionality for the history service.
 */

public class ChangeLogOperator {

    private static final String SELECT_ALL_CHANGELOGS_N1QL = "SELECT DATABASECHANGELOG.* from DATABASECHANGELOG " +
            "ORDER BY orderExecuted ASC";
    private static final String SELECT_LAST_ORDER_N1QL = "SELECT orderExecuted from DATABASECHANGELOG " +
            "order by orderExecuted DESC LIMIT 1";
    private static final int NO_ORDER = 0;

    private final CouchbaseLiquibaseDatabase database;
    private final ServiceProvider serviceProvider;

    public ChangeLogOperator(CouchbaseLiquibaseDatabase database) {
        this.database = database;
        this.serviceProvider = new ContextServiceProvider(database);
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
        StringBuilder id = new StringBuilder();
        id.append(changeLog.getFileName()).append("::");
        id.append(changeLog.getId()).append("::");
        id.append(changeLog.getAuthor());

        Collection collection = serviceProvider.getServiceCollection(CHANGE_LOG_COLLECTION);
        collection.insert(id.toString(), changeLog);
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
        List<CouchbaseChangeLog> changeLogs = scope.query(SELECT_ALL_CHANGELOGS_N1QL, queryOptions)
                .rowsAs(CouchbaseChangeLog.class);

        return changeLogs.stream()
                .map(ChangeSetMapper::mapToRanChangeSet).collect(Collectors.toList());
    }

}
