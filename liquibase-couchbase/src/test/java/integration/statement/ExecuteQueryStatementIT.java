package integration.statement;

import com.couchbase.client.core.error.ParsingFailureException;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.ExecuteQueryStatement;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.matchers.CouchbaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ExecuteQueryStatementIT extends RandomizedScopeTestCase {

    @Test
    void Should_execute_query() {
        ExecuteQueryStatement executeQueryStatement = new ExecuteQueryStatement("CREATE COLLECTION `testBucket`._default.test1");
        executeQueryStatement.execute(clusterOperator);
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
        assertThat(bucketOperator.getBucket()).hasCollectionInScope("test1", "_default");
        bucketOperator.dropCollectionInDefaultScope("test1");
    }

    @Test
    void Should_throw_exception() {
        ExecuteQueryStatement executeQueryStatement = new ExecuteQueryStatement("Wrong query");
        assertThatExceptionOfType(ParsingFailureException.class)
                .isThrownBy(() -> executeQueryStatement.execute(clusterOperator));
    }
}
