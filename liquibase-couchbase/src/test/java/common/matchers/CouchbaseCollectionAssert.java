package common.matchers;

import com.couchbase.client.java.Collection;

import org.assertj.core.api.AbstractAssert;

import java.util.List;

import liquibase.ext.couchbase.types.Document;
import lombok.NonNull;

public class CouchbaseCollectionAssert extends AbstractAssert<CouchbaseCollectionAssert, Collection> {

    private CouchbaseCollectionAssert(Collection collection) {
        super(collection, CouchbaseCollectionAssert.class);
    }

    public static CouchbaseCollectionAssert assertThat(@NonNull Collection actual) {
        return new CouchbaseCollectionAssert(actual);
    }

    public CouchbaseCollectionAssert hasDocument(@NonNull String id) {
        if (!actual.exists(id).exists()) {
            failWithMessage("Collection [<%s>] doesn't contains document with ID [<%s>] in scope [<%s>]",
                    actual.name(),
                    id,
                    actual.scopeName()
            );
        }

        return this;
    }

    public CouchbaseCollectionAssert hasNoDocument(@NonNull String id) {
        if (actual.exists(id).exists()) {
            failWithMessage("Collection [<%s>] contains document with ID [<%s>] in scope [<%s>]",
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

    public CouchBaseDocumentAssert extractingDocument(@NonNull String id) {
        hasDocument(id);

        return new CouchBaseDocumentAssert(actual.get(id).contentAsObject());
    }


    public CouchbaseCollectionAssert containDocuments(List<Document> testDocuments) {
        testDocuments.forEach((doc) -> extractingDocument(doc.getId()).itsContentEquals(doc.getContent()));

        return this;
    }
}
