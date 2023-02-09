package common.matchers;

import com.couchbase.client.java.manager.query.QueryIndex;
import com.couchbase.client.java.manager.query.QueryIndexManager;
import org.assertj.core.api.AbstractAssert;

import java.util.List;

public class QueryIndexAssert extends AbstractAssert<QueryIndexAssert, QueryIndexManager> {

    private String bucketName;

    public QueryIndexAssert(QueryIndexManager actual) {
        super(actual, QueryIndexAssert.class);
    }

    public QueryIndexAssert(QueryIndexManager queryIndexManager,
                            List<QueryIndex> indexes,
                            String bucketName) {
        this(queryIndexManager);
        this.bucketName = bucketName;
    }

    public QueryIndexAssert doesNotHave(String indexName) {
        boolean contains = actual.getAllIndexes(bucketName)
                .stream()
                .map(QueryIndex::name)
                .anyMatch(indexName::equals);
        if (contains) {
            failWithMessage("Bucket <%s> has index named <%s>, but it shouldn't",
                    bucketName,
                    indexName);
        }

        return this;
    }

    public QueryIndexAssert has(String indexName) {
        boolean contains = actual.getAllIndexes(bucketName).stream().map(QueryIndex::name).anyMatch(indexName::equals);
        if (!contains) {
            failWithMessage("Bucket <%s> doesn't have index named <%s> , but it shouldn't",
                    bucketName,
                    indexName);
        }

        return this;
    }

    public QueryIndexAssert doesNotHavePrimary() {
        boolean hasPrimary = actual.getAllIndexes(bucketName)
                .stream()
                .filter(it -> bucketName.equals(it.bucketName()))
                .anyMatch(QueryIndex::primary);
        if (hasPrimary) {
            failWithMessage("Bucket <%s> has primary index , but it shouldn't", bucketName);
        }

        return this;
    }

    public QueryIndexAssert hasPrimaryIndexForName(String indexName) {
        long count = actual.getAllIndexes(bucketName).stream()
                .filter(index -> index.name().equals(indexName) && index.primary()).count();
        if (count != 1) {
            failWithMessage("Primary index with the name <%s> is absent", indexName);
        }
        return this;
    }

    public QueryIndexAssert hasQueryIndexForName(String indexName) {
        long count = actual.getAllIndexes(bucketName).stream().filter(index -> index.name().equals(indexName)
                && !index.primary()).count();
        if (count != 1) {
            failWithMessage("Query index with name <%s> is absent in bucket <%s> or more than one", indexName, bucketName);
        }
        return this;
    }
}
