package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.UpdateBucketOptions;
import liquibase.Scope;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UpdateBucketStatement extends CouchbaseStatement {

    private final Logger logger = Scope.getCurrentScope().getLog(UpdateBucketStatement.class);
    private final UpdateBucketOptions options;
    private final BucketSettings settings;

    @Override
    public void execute(ClusterOperator operator) {
        logger.info(String.format("Updating the <%s> bucket", settings.name()));
        operator.updateBucketWithOptionsAndSettings(settings, options);
    }
}
