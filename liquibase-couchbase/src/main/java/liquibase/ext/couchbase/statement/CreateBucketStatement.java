package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import liquibase.Scope;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;


@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateBucketStatement extends CouchbaseStatement {
    private static final String existsMsg = "Bucket %s already exists, skipping creation";
    private final Logger logger = Scope.getCurrentScope().getLog(CreateBucketStatement.class);
    private final CreateBucketOptions options;
    private final BucketSettings settings;

    @Override
    public void execute(ClusterOperator operator) {
        operator.createBucketWithOptionsAndSettings(settings, options);
    }
}
