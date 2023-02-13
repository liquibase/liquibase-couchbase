package common.matchers;

import com.couchbase.client.core.error.DocumentNotFoundException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import liquibase.changelog.ChangeSet;
import liquibase.ext.couchbase.changelog.CouchbaseChangeLog;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

import static liquibase.ext.couchbase.provider.ServiceProvider.CHANGE_LOG_COLLECTION;


public class ChangeLogAssert extends AbstractAssert<ChangeLogAssert, Scope> {

    private String key;
    private Scope scope;
    private Collection collection;

    private CouchbaseChangeLog changeLog;

    private ChangeLogAssert(Scope scope) {
        super(scope, ChangeLogAssert.class);
        this.scope = scope;
        this.collection = scope.collection(CHANGE_LOG_COLLECTION);
    }


    public static ChangeLogAssert assertThat(@NonNull Scope scope) {
        return new ChangeLogAssert(scope);
    }

    public ChangeLogAssert documentsSizeEqualTo(@NonNull int numberOfDocuments) {
        List<JsonObject> documents = scope.query("Select * from DATABASECHANGELOG").rowsAsObject();
        if (documents.size() != numberOfDocuments) {
            failWithMessage("Size of documents not equals to <%s>, actual number is <%s>", numberOfDocuments,
                    documents.size());
        }

        return this;
    }

    public ChangeLogAssert hasDocument(@NonNull String key) {
        try {
            changeLog = collection.get(key).contentAs(CouchbaseChangeLog.class);
            this.key = key;
        } catch (DocumentNotFoundException ex) {
            failWithMessage("ChangeLog with key <%s> not exists", key);
        }

        return this;
    }

    public ChangeLogAssert withExecType(@NonNull ChangeSet.ExecType execType) {
        if (!changeLog.getExecType().equals(execType)) {
            failWithMessage("ChangeLog with key <%s> doesn't contain execType <%s>, actual type is <%s>", key,
                    execType, changeLog.getExecType());
        }

        return this;
    }

    public ChangeLogAssert withOrder(@NonNull int order) {
        if (changeLog.getOrderExecuted() != order) {
            failWithMessage("ChangeLog with key <%s> doesn't have order <%s>, actual order is <%s>", key,
                    order, changeLog.getOrderExecuted());
        }

        return this;
    }

}
