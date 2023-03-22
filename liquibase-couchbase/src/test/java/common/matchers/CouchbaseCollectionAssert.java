package common.matchers;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.Collection;
import liquibase.ext.couchbase.lockservice.CouchbaseLock;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Id;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

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

    public CouchbaseCollectionAssert doesNotContainIds(@NonNull List<Id> ids) {
        for (Id id : ids) {
            doesNotContainId(id.getId());
        }
        return this;
    }

    public CouchbaseCollectionAssert containsAnyId(@NonNull List<Id> ids) {
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
        extractingDocument(doc.getId()).itsContentEquals(doc.getContentAsJson());

        return this;
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
}
