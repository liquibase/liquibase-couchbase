package common.matchers;

import com.couchbase.client.java.Collection;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

import java.util.Map;
import java.util.Objects;

public class CouchbaseCollectionAssert extends AbstractAssert<CouchbaseCollectionAssert, Collection> {
    private String id;

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
        this.id = id;
        return this;
    }

    public CouchbaseCollectionAssert hasDocuments(@NonNull String... ids) {
        for (String id : ids) {
            hasDocument(id);
        }
        return this;
    }

    public CouchbaseCollectionAssert hasDocuments(@NonNull Map<String, String> documents) {
        for (Map.Entry<String, String> entry : documents.entrySet()) {
            hasDocument(entry.getKey());
            itsContentEquals(entry.getValue());
        }
        return this;
    }

    public CouchbaseCollectionAssert itsContentEquals(@NonNull String content) {
        if (Objects.isNull(this.id)) {
            failWithMessage("No document to check was provided");
        }
        String actualContent = actual.get(id).contentAs(String.class);
        if (!content.equals(actualContent)) {
            failWithMessage("Unexpected content for document <%s>, expected <%s>, actual <%s>",
                    id,
                    content,
                    actualContent
            );
        }
        return this;
    }


}
