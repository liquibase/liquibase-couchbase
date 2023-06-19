package common.matchers;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import liquibase.changelog.ChangeSet;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;


public class ChangeLogAssert extends AbstractAssert<ChangeLogAssert, Scope> {

    private static final String SELECT_ALL_HISTORY_DOCUMENTS_QUERY = "SELECT * FROM DATABASECHANGELOG";

    private String key;
    private final Scope scope;
    private final Collection collection;

    private CouchbaseChangeLog changeLog;

    private ChangeLogAssert(Scope scope) {
        super(scope, ChangeLogAssert.class);
        this.scope = scope;
        this.collection = scope.collection(CHANGE_LOG_COLLECTION);
    }


    public static ChangeLogAssert assertThat(@NonNull Scope scope) {
        return new ChangeLogAssert(scope);
    }

    public ChangeLogAssert documentsSizeEqualTo(int numberOfDocuments) {
        QueryOptions queryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        List<JsonObject> documents = scope.query(SELECT_ALL_HISTORY_DOCUMENTS_QUERY, queryOptions).rowsAsObject();
        if (documents.size() != numberOfDocuments) {
            failWithMessage("The size of documents is not equal to [%s], the actual number is [%s]", numberOfDocuments,
                    documents.size());
        }

        return this;
    }

    public ChangeLogAssert hasDocument(@NonNull String key) {
        try {
            changeLog = collection.get(key).contentAs(CouchbaseChangeLog.class);
            this.key = key;
        } catch (DocumentNotFoundException ex) {
            failWithMessage("A changelog with key [%s] doesn't exist", key);
        }

        return this;
    }

    public ChangeLogAssert hasDocuments(@NonNull String... keys) {
        for (String key : keys) {
            hasDocument(key);
        }
        return this;
    }

    public ChangeLogAssert hasNoDocument(@NonNull String key) {
        try {
            changeLog = collection.get(key).contentAs(CouchbaseChangeLog.class);
            failWithMessage("A changelog with key [%s] exists", key);
        } catch (DocumentNotFoundException ignored) {
        }

        return this;
    }

    public ChangeLogAssert withExecType(@NonNull ChangeSet.ExecType execType) {
        if (!changeLog.getExecType().equals(execType)) {
            failWithMessage("A changelog with key [%s] doesn't contain an execType [%s], the actual type is [%s]", key,
                    execType, changeLog.getExecType());
        }

        return this;
    }

    public ChangeLogAssert withOrder(@NonNull int order) {
        if (changeLog.getOrderExecuted() != order) {
            failWithMessage("A changelog with key [%s] doesn't have an order [%s], the actual order is [%s]", key,
                    order, changeLog.getOrderExecuted());
        }

        return this;
    }

    public ChangeLogAssert withTag(@NonNull String tag) {
        if (!changeLog.getTag().equals(tag)) {
            failWithMessage("A changelog with key [%s] doesn't have tag [%s]", key, tag);
        }

        return this;
    }

    public ChangeLogAssert withComments(@NonNull String comments) {
        if (!changeLog.getComments().equals(comments)) {
            failWithMessage("A changelog with key [%s] doesn't have comments [%s]", key, comments);
        }

        return this;
    }

}
