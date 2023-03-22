package common.matchers;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryResult;
import lombok.NonNull;
import org.assertj.core.api.AbstractAssert;

public class CouchBaseQueryResultAssert extends AbstractAssert<CouchBaseQueryResultAssert, QueryResult> {

    CouchBaseQueryResultAssert(QueryResult queryResult) {
        super(queryResult, CouchBaseQueryResultAssert.class);
    }

    public static CouchBaseQueryResultAssert assertThat(@NonNull QueryResult actual) {
        return new CouchBaseQueryResultAssert(actual);
    }

    public CouchBaseQueryResultAssert hasSize(int size) {
        int actualSize = actual.rowsAsObject().size();
        if (actualSize != size) {
            failWithMessage("Unexpected documents size in collection, expected is [%d], actual is [%d]",
                    size,
                    actualSize);
        }
        return this;
    }

    public CouchBaseQueryResultAssert isEmpty() {
        boolean isEmpty = actual.rowsAsObject().isEmpty();
        if (!isEmpty) {
            failWithMessage("Unexpected result of query result %s , should be empty!", actual);
        }
        return this;
    }

    public CouchBaseQueryResultAssert areContentsEqual(JsonObject[] expectedObjs, String collection) {
        for (int i = 0; i < expectedObjs.length; i++) {
            isContentEqual(i, expectedObjs[i], collection);
        }
        return this;
    }


    public CouchBaseQueryResultAssert isContentEqual(int docIndex, JsonObject expectedObj, String collection) {
        JsonObject actualObj = actual.rowsAsObject().get(docIndex).getObject(collection);
        if (!expectedObj.equals(actualObj)) {
            failWithMessage("Unexpected content for document, expected is [%s], actual is [%s]",
                    expectedObj,
                    actualObj);
        }
        return this;
    }

}
