package integration.statement;

import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.change.CreateBucketChange;
import liquibase.ext.couchbase.change.UpdateBucketChange;
import liquibase.ext.couchbase.change.utils.BucketCreationMapper;
import liquibase.ext.couchbase.change.utils.BucketUpdateMapper;
import liquibase.ext.couchbase.exception.BucketNotExistException;
import liquibase.ext.couchbase.operator.ClusterOperator;
import liquibase.ext.couchbase.statement.UpdateBucketStatement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static common.change.TestChangeFactory.prepareCreateBucketChange;
import static common.change.TestChangeFactory.prepareUpdateBucketChange;
import static common.constants.TestConstants.UPDATE_TEST_BUCKET;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


@Slf4j
class UpdateBucketStatementTestIT extends ConstantScopeTestCase {

    @AfterEach
    void clean() {
        if (clusterOperator.isBucketExists(UPDATE_TEST_BUCKET)) {
            clusterOperator.dropBucket(UPDATE_TEST_BUCKET);
        }
    }


    @Test
    void Should_update_existing_bucket() {
        log.info("Prepare test bucket '{}'", UPDATE_TEST_BUCKET);
        ClusterOperator operator = new ClusterOperator(cluster);
        CreateBucketChange createBucketChange = prepareCreateBucketChange(UPDATE_TEST_BUCKET);
        BucketCreationMapper bucketCreationMapper = new BucketCreationMapper(createBucketChange);

        operator.createBucketWithOptionsAndSettings(bucketCreationMapper.bucketSettings(), bucketCreationMapper.bucketOptions());

        log.info("Updating test bucket '{}'", UPDATE_TEST_BUCKET);
        UpdateBucketChange updateBucketChange = prepareUpdateBucketChange(UPDATE_TEST_BUCKET);
        BucketUpdateMapper bucketUpdateMapper = new BucketUpdateMapper(updateBucketChange);

        UpdateBucketStatement updateBucketStatement = new UpdateBucketStatement(bucketUpdateMapper.bucketOptions(),
                bucketUpdateMapper.bucketSettings());
        updateBucketStatement.execute(operator);
        assertThat(cluster).bucketUpdatedSuccessfully(UPDATE_TEST_BUCKET, bucketUpdateMapper.bucketSettings());
    }

    @Test
    void Should_throw_error_when_update_non_existing_bucket() {
        UpdateBucketChange updateBucketChange = prepareUpdateBucketChange(UPDATE_TEST_BUCKET);
        BucketUpdateMapper bucketUpdateMapper = new BucketUpdateMapper(updateBucketChange);
        UpdateBucketStatement statement = new UpdateBucketStatement(bucketUpdateMapper.bucketOptions(),
                bucketUpdateMapper.bucketSettings());

        assertThatExceptionOfType(BucketNotExistException.class)
                .isThrownBy(() -> statement.execute(new ClusterOperator(cluster)))
                .withMessage("Bucket [%s] not exists", UPDATE_TEST_BUCKET);
    }


}