package common.matchers;

import com.couchbase.client.java.json.JsonObject;

import org.assertj.core.api.AbstractAssert;

import lombok.NonNull;

public class CouchBaseDocumentAssert extends AbstractAssert<CouchBaseDocumentAssert, JsonObject> {

    CouchBaseDocumentAssert(JsonObject collection) {
        super(collection, CouchBaseDocumentAssert.class);
    }

    public CouchBaseDocumentAssert itsContentEquals(@NonNull JsonObject expected) {
        if (!actual.equals(expected)) {
            failWithMessage("Unexpected content for document , expected <%s>, actual <%s>",
                    expected,
                    actual
            );
        }
        return this;
    }

    public CouchBaseDocumentAssert itsContentEquals(@NonNull String expected) {
        if (!actual.toString().equals(expected)) {
            failWithMessage("Unexpected content for document , expected <%s>, actual <%s>",
                    expected,
                    actual
            );
        }
        return this;
    }
}
