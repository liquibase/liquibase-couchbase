package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import liquibase.ext.couchbase.operator.ClusterOperator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateBucketStatement extends CouchbaseStatement {

    private final CreateBucketOptions options;
    private final BucketSettings settings;

    @Override
    public void execute(ClusterOperator operator) {
        operator.createBucketWithOptionsAndSettings(settings, options);
    }
}
