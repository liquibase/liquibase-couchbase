package liquibase.ext.couchbase.statement;

import com.couchbase.client.java.manager.bucket.BucketSettings;
import com.couchbase.client.java.manager.bucket.CreateBucketOptions;
import liquibase.Scope;
import liquibase.ext.couchbase.exception.BucketExistsException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.logging.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@Data
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateBucketStatement extends CouchbaseStatement {
    private static final String existsMsg = "Bucket %s already exists, skipping creation";
    private final Logger logger = Scope.getCurrentScope().getLog(CreateBucketStatement.class);
    private final CreateBucketOptions options;
    private final BucketSettings settings;
    private final boolean ignoreIfExists;

    @Override
    public void execute(ClusterOperator operator) {
        boolean bucketExists = operator.isBucketExists(settings.name());
        if (ignoreIfExists && bucketExists) {
            logger.info(format(existsMsg, settings.name()));
            return;
        }
        if (bucketExists) {
            throw new BucketExistsException(settings.name());
        }
        operator.createBucketWithOptionsAndSettings(settings, options);
    }
}
