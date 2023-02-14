package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;
import liquibase.ext.couchbase.exception.CollectionNotExistsException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import static com.couchbase.client.java.manager.collection.CollectionSpec.create;

/**
 * Common facade on {@link Bucket} including all common operations <br >
 * and state checks
 */
@RequiredArgsConstructor
public class BucketOperator {

    //TODO split to BucketValidator and Bucket

    @Getter
    private final Bucket bucket;

    public boolean hasCollectionInScope(@NonNull String collectionName, @NonNull String scopeName) {
        return presentsInScope(collectionName, scopeName);
    }

    public boolean hasCollectionInDefaultScope(@NonNull String collectionName) {
        return presentsInScope(collectionName, bucket.defaultScope().name());
    }

    public void requireScopeExists(@NonNull String collectionName, @NonNull String scopeName)
            throws CollectionNotExistsException {
        boolean isExists = hasCollectionInScope(collectionName, scopeName);
        if (!isExists) {
            throw new CollectionNotExistsException(collectionName, scopeName);
        }
    }

    public void createCollection(String collectionName, String scopeName) {
        bucket.collections().createCollection(create(collectionName, scopeName));
    }

    public void createCollectionInDefaultScope(String name) {
        createCollection(name, bucket.defaultScope().name());
    }

    public void dropCollection(String name, String scope) {
        getBucket().collections().dropCollection(create(name, scope));
    }

    public void dropCollectionInDefaultScope(String name) {
        dropCollection(name, bucket.defaultScope().name());
    }

    public Collection getCollection(String name, String scope) {
        return getBucket().scope(scope).collection(name);
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
