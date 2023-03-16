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

    public CouchbaseCollectionAssert hasDocument(@NonNull String id) {
        if (!actual.exists(id).exists()) {
            failWithMessage("Collection [%s] doesn't contain document with ID [%s] in the scope [%s]",
                    actual.name(),
                    id,
                    actual.scopeName()
            );
        }

        return this;
    }

    public CouchbaseCollectionAssert hasNoDocument(@NonNull String id) {
        if (actual.exists(id).exists()) {
            failWithMessage("Collection [%s] contains document with ID [%s] in the scope [%s]",
                    actual.name(),
                    id,
                    actual.scopeName()
            );
        }

        return this;
    }

    public CouchbaseCollectionAssert hasDocuments(@NonNull String... ids) {
        for (String id : ids) {
            hasDocument(id);
        }
        return this;
    }

    public CouchbaseCollectionAssert hasAnyDocument(@NonNull List<Id> ids) {
        if (ids.stream().noneMatch(id -> actual.exists(id.getId()).exists())) {
            failWithMessage("Collection [%s] in the scope [%s] doesn't contain any documents from list [%s]",
                    actual.name(),
                    actual.scopeName(),
                    ids
            );
        }
        return this;
    }

    public CouchbaseCollectionAssert hasNoDocuments(@NonNull String... ids) {
        for (String id : ids) {
            hasNoDocument(id);
        }
        return this;
    }

    public CouchbaseCollectionAssert hasNoDocuments(@NonNull List<Document> docs) {
        for (Document doc : docs) {
            hasNoDocument(doc.getId());
        }
        return this;
    }

    public CouchbaseCollectionAssert hasNoDocumentsByIds(@NonNull List<Id> ids) {
        for (Id id : ids) {
            hasNoDocument(id.getId());
        }
        return this;
    }

    public CouchBaseDocumentAssert extractingDocument(@NonNull String id) {
        hasDocument(id);

        return new CouchBaseDocumentAssert(actual.get(id).contentAsObject());
    }

    public CouchbaseCollectionAssert containDocuments(List<Document> testDocuments) {
        testDocuments.forEach((doc) -> extractingDocument(doc.getId()).itsContentEquals(doc.getValue()));

        return this;
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
