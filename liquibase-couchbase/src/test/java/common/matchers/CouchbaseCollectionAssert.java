package common.matchers;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.json.JsonArray;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.manager.query.QueryIndex;
import liquibase.ext.couchbase.lockservice.CouchbaseLock;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import liquibase.ext.couchbase.types.Value;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.List;
import java.util.Set;

public class CouchbaseCollectionAssert extends AbstractAssert<CouchbaseCollectionAssert, Collection> {

    private CouchbaseCollectionAssert(Collection collection) {
        super(collection, CouchbaseCollectionAssert.class);
    }

    public static CouchbaseCollectionAssert assertThat(@NonNull Collection actual) {
        return new CouchbaseCollectionAssert(actual);
    }

    public CouchbaseCollectionAssert containsId(@NonNull String id) {
        if (!actual.exists(id).exists()) {
            failWithMessage("Collection [%s] doesn't contain document with ID [%s] in the scope [%s]",
                    actual.name(),
                    id,
                    actual.scopeName()
            );
        }

        return this;
    }

    public CouchbaseCollectionAssert doesNotContainId(@NonNull String id) {
        if (actual.exists(id).exists()) {
            failWithMessage("Collection [%s] contains document with ID [%s] in the scope [%s]",
                    actual.name(),
                    id,
                    actual.scopeName()
            );
        }

        return this;
    }

    public CouchbaseCollectionAssert containsIds(@NonNull String... ids) {
        for (String id : ids) {
            containsId(id);
        }
        return this;
    }

    public CouchbaseCollectionAssert doesNotContainIds(@NonNull String... ids) {
        for (String id : ids) {
            doesNotContainId(id);
        }
        return this;
    }

    public CouchbaseCollectionAssert doesNotContainIds(@NonNull Set<Id> ids) {
        for (Id id : ids) {
            doesNotContainId(id.getId());
        }
        return this;
    }

    public CouchbaseCollectionAssert containsAnyId(@NonNull Set<Id> ids) {
        if (ids.stream().noneMatch(id -> actual.exists(id.getId()).exists())) {
            failWithMessage("Collection [%s] in the scope [%s] doesn't contain any documents from list [%s]",
                    actual.name(),
                    actual.scopeName(),
                    ids
            );
        }
        return this;
    }

    public CouchbaseCollectionAssert contains(@NonNull List<Document> docs) {
        docs.forEach(this::contains);
        return this;
    }

    public CouchbaseCollectionAssert contains(Document doc) {
        extractingDocument(doc.getId(), getClassName(doc.getValue())).itsContentEquals(doc.getValue());

        return this;
    }

    public CouchbaseDocumentAssert containsDocument(Document doc) {
        extractingDocument(doc.getId(), getClassName(doc.getValue())).itsContentEquals(doc.getContentAsJson());

        return extractingDocument(doc.getId(), getClassName(doc.getValue()));
    }

    private Class getClassName(Value value) {
        switch (value.getType()) {
            case JSON:
                return JsonObject.class;
            case JSON_ARRAY:
                return JsonArray.class;
            case STRING:
                return String.class;
            case LONG:
                return Long.class;
            case DOUBLE:
                return Double.class;
            case BOOLEAN:
                return Boolean.class;
            default:
                throw new RuntimeException("Class not found");
        }
    }

    public CouchbaseCollectionAssert doesNotContain(Document doc) {
        doesNotContainId(doc.getId());

        return this;
    }

    public CouchbaseCollectionAssert doesNotContain(@NonNull List<Document> docs) {
        for (Document doc : docs) {
            doesNotContainId(doc.getId());
        }
        return this;
    }

    public CouchbaseDocumentAssert extractingDocument(@NonNull String id, @NonNull Class clazz) {
        containsId(id);

        return new CouchbaseDocumentAssert(actual.get(id).contentAs(clazz));
    }

    public CouchbaseDocumentAssert extractingDocument(@NonNull String id) {
        containsId(id);

        return new CouchbaseDocumentAssert(actual.get(id).contentAsObject());
    }

    public CouchbaseCollectionAssert hasLockHeldBy(String lockId, String owner) {
        try {
            boolean owns = actual.get(lockId)
                    .contentAs(CouchbaseLock.class)
                    .getOwner().equals(owner);
            if (!owns) {
                failWithMessage("[%s] is not an owner of the lock [%s]", owner, lockId);
            }
        } catch (CouchbaseException e) {
            failWithMessage("No such lock with ID [%s]", lockId);
        }

        return this;
    }

    public CouchbaseCollectionAssert hasIndex(String name) {
        boolean indexExists = actual.queryIndexes().getAllIndexes().stream()
                .map(QueryIndex::name)
                .anyMatch(name::equals);
        if (!indexExists) {
            failWithMessage("[%s] index doesn't exist in `[%s].[%s].[%s]` keyspace", name, actual.bucketName(),
                    actual.scopeName(), actual.name());
        }
        return this;
    }

    public CouchbaseCollectionAssert hasNoIndex(String queryIndexName) {
        boolean indexExists = actual.queryIndexes().getAllIndexes().stream()
                .map(QueryIndex::name)
                .anyMatch(queryIndexName::equals);
        if (indexExists) {
            failWithMessage("[%s] index exists in `[%s].[%s].[%s]` keyspace", queryIndexName, actual.bucketName(),
                    actual.scopeName(), actual.name());
        }
        return this;
    }
}
