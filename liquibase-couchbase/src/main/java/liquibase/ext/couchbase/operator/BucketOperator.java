package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;

/**
 * A part of a facade package for Couchbase Java SDK. Provides access to {@link Bucket} common operations and state checks.
 */
@RequiredArgsConstructor
public class BucketOperator {

    // TODO split to BucketValidator and Bucket

    @Getter
    protected final Bucket bucket;

    public CollectionOperator getCollectionOperator(String collectionName, String scopeName) {
        return new CollectionOperator(bucket.scope(scopeName).collection(collectionName));
    }

    public void createScope(String name) {
        bucket.collections().createScope(name);
    }

    public Scope getScope(String scopeName) {
        return bucket.scope(scopeName);
    }

    public Scope getOrCreateScope(String scopeName) {
        if (!hasScope(scopeName)) {
            createScope(scopeName);
        }
        return getScope(scopeName);
    }

    public void dropScope(String scopeName) {
        bucket.collections().dropScope(scopeName);
    }

    public boolean hasScope(String name) {
        return bucket.collections().getAllScopes().stream().anyMatch(scopeSpec -> scopeSpec.name().equals(name));
    }

    public boolean hasCollectionInScope(@NonNull String collectionName, @NonNull String scopeName) {
        return presentsInScope(collectionName, scopeName);
    }

    public boolean hasCollectionInDefaultScope(@NonNull String collectionName) {
        return presentsInScope(collectionName, bucket.defaultScope().name());
    }

    public void createCollection(String collectionName, String scopeName) {
        bucket.collections().createCollection(create(collectionName, scopeName));
    }

    public void createCollectionInDefaultScope(String name) {
        createCollection(name, bucket.defaultScope().name());
    }

    public void dropCollection(String collectionName, String scopeName) {
        getBucket().collections().dropCollection(create(collectionName, scopeName));
    }

    public void dropCollectionInDefaultScope(String name) {
        dropCollection(name, bucket.defaultScope().name());
    }

    public Collection getCollection(String collectionName, String scopeName) {
        return getBucket().scope(scopeName).collection(collectionName);
    }

    public Collection getCollectionFromDefaultScope(String name) {
        return getCollection(name, bucket.defaultScope().name());
    }

    private boolean presentsInScope(String collectionName, String scopeName) {
        return bucket.collections()
                .getAllScopes()
                .stream()
                .map(ScopeSpec::collections)
                .flatMap(java.util.Collection::stream)
                .filter(it -> it.scopeName().equals(scopeName))
                .map(CollectionSpec::name)
                .anyMatch(collectionName::equals);
    }

}
