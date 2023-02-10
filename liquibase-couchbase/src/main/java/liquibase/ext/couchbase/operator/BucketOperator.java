package liquibase.ext.couchbase.operator;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.manager.collection.CollectionSpec;
import com.couchbase.client.java.manager.collection.ScopeSpec;

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

    public boolean hasCollectionDefaultInScope(@NonNull String collectionName) {
        return presentsInScope(collectionName, bucket.defaultScope().name());
    }

    public void createCollection(String collectionName, String scopeName) {
        bucket.collections().createCollection(create(collectionName, scopeName));
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
