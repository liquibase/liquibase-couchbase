package common.matchers;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;

import org.assertj.core.api.AbstractAssert;

import java.util.List;

import lombok.NonNull;

public class CouchbaseBucketAssert extends AbstractAssert<CouchbaseBucketAssert, Bucket> {

    private final List<ScopeSpec> scopes;

    private CouchbaseBucketAssert(Bucket bucket) {
        super(bucket, CouchbaseBucketAssert.class);
        scopes = actual.collections().getAllScopes();
    }


    public static CouchbaseBucketAssert assertThat(@NonNull Bucket actual) {
        return new CouchbaseBucketAssert(actual);
    }

    public CouchbaseBucketAssert hasCollectionInDefaultScope(@NonNull String collectionName) {
        if (!presentsInScope(collectionName, actual.defaultScope().name())) {
            failWithMessage("Collection <%s> not exists in bucket <%s> in scope <%s>",
                    collectionName,
                    actual.name(),
                    actual.defaultScope().name()
            );
        }

        return this;
    }

    public CouchbaseBucketAssert hasCollectionInScope(@NonNull String collectionName,
                                                      @NonNull String scopeName) {
        if (!presentsInScope(collectionName, scopeName)) {
            failWithMessage("Collection <%s> not exists in bucket <%s> in scope <%s>",
                    collectionName,
                    actual.name(),
                    scopeName
            );
        }

        return this;
    }

    private boolean presentsInScope(String collectionName, String scopeName) {
        return scopes.stream()
                .map(ScopeSpec::collections)
                .flatMap(java.util.Collection::stream)
                .filter(it -> it.scopeName().equals(scopeName))
                .map(CollectionSpec::name)
                .anyMatch(collectionName::equals);
    }
}
