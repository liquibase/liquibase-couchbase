package common.matchers;

import com.couchbase.client.java.json.JsonObject;

import liquibase.ext.couchbase.types.Value;
import org.assertj.core.api.AbstractAssert;

import lombok.NonNull;

public class CouchbaseDocumentAssert extends AbstractAssert<CouchbaseDocumentAssert, Object> {

    CouchbaseDocumentAssert(Object document) {
        super(document, CouchbaseDocumentAssert.class);
    }

    public CouchbaseDocumentAssert itsContentEquals(@NonNull JsonObject expected) {
        if (!actual.equals(expected)) {
            failWithMessage("Unexpected content for document, expected is [%s], actual is [%s]",
                    expected,
                    actual
            );
        }
        return this;
    }

    public CouchbaseDocumentAssert itsContentEquals(@NonNull Value expected) {
        if (!actual.equals(expected.mapDataToType())) {
            failWithMessage("Unexpected content for document, expected is [%s], actual is [%s]",
                    expected,
                    actual
            );
        }
        return this;
    }

    public CouchbaseDocumentAssert itsContentEquals(@NonNull String expected) {
        if (!actual.toString().equals(expected)) {
            failWithMessage("Unexpected content for document, expected is [%s], actual is [%s]",
                    expected,
                    actual
            );
        }
        return this;
    }

    public CouchbaseDocumentAssert jsonHasNoField(@NonNull String name) {
        if (((JsonObject) actual).containsKey(name)) {
            failWithMessage("JsonObject [%s] should not contain key [%s], but it does",
                    actual,
                    name
            );
        }
        return this;
    }

    public CouchbaseDocumentAssert jsonHasField(@NonNull String name) {
        if (!((JsonObject) actual).containsKey(name)) {
            failWithMessage("JsonObject [%s] should contain key [%s], but it doesn't",
                    actual,
                    name
            );
        }
        return this;
    }
}
