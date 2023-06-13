package liquibase.ext.couchbase.types;

import liquibase.serializer.AbstractLiquibaseSerializable;
import lombok.Data;
import lombok.NonNull;

import static com.couchbase.client.core.io.CollectionIdentifier.DEFAULT_SCOPE;

/**
 * Class to  bucket-scope pair to perform couchbase sdk operations without null check. Default scope value is provided if not specified
 */
@Data
public class BucketScope extends AbstractLiquibaseSerializable {

    private final String bucket;
    private final String scope;

    private BucketScope(@NonNull String bucket,
                        @NonNull String scope) {
        this.bucket = bucket;
        this.scope = scope;
    }

    public static BucketScope bucketScope(String bucket, String scope) {
        return new BucketScope(bucket, scope);
    }

    @Override
    public String getSerializedObjectName() {
        return "bucketScope";
    }

    @Override
    public String getSerializedObjectNamespace() {
        return STANDARD_CHANGELOG_NAMESPACE;
    }
}
