package common.matchers;

import com.couchbase.client.java.Bucket;

import org.assertj.core.api.AbstractAssert;

import liquibase.ext.couchbase.operator.BucketOperator;
import lombok.NonNull;

public class CouchbaseBucketAssert extends AbstractAssert<CouchbaseBucketAssert, Bucket> {

    private final BucketOperator bucketOperator;

    private CouchbaseBucketAssert(Bucket bucket) {
        super(bucket, CouchbaseBucketAssert.class);
        bucketOperator = new BucketOperator(bucket);
    }


    public static CouchbaseBucketAssert assertThat(@NonNull Bucket actual) {
        return new CouchbaseBucketAssert(actual);
    }

    public CouchbaseBucketAssert hasCollectionInDefaultScope(@NonNull String collectionName) {
        if (!bucketOperator.hasCollectionInDefaultScope(collectionName)) {
            failWithMessage("Collection [%s] doesn't exist in the bucket [%s] in the scope [%s]",
                    collectionName,
                    actual.name(),
                    actual.defaultScope().name()
            );
        }

        return this;
    }

    public CouchbaseBucketAssert hasCollectionInScope(@NonNull String collectionName,
                                                      @NonNull String scopeName) {
        if (!bucketOperator.hasCollectionInScope(collectionName, scopeName)) {
            failWithMessage("Collection [%s] doesn't exist in the bucket [%s] in the scope [%s]",
                    collectionName,
                    actual.name(),
                    scopeName
            );
        }

        return this;
    }

    public CouchbaseBucketAssert hasNoCollectionInScope(@NonNull String collectionName,
                                                        @NonNull String scopeName) {
        if (bucketOperator.hasCollectionInScope(collectionName, scopeName)) {
            failWithMessage("Collection [%s] exists in the bucket [%s] in the scope [%s]",
                    collectionName,
                    actual.name(),
                    scopeName
            );
        }

        return this;
    }

}
