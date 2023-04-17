package system.change;

import com.couchbase.client.core.service.ServiceScope;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryScanConsistency;
import common.operators.TestBucketOperator;
import liquibase.Liquibase;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static common.constants.ChangeLogSampleFilePaths.CREATE_QUERY_INDEX_TEST_XML;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.provider.ServiceProvider.DEFAULT_SERVICE_SCOPE;
import static liquibase.ext.couchbase.provider.ServiceProvider.SERVICE_BUCKET_NAME;

public class CreateQueryIndexSystemTest extends LiquibaseSystemTest {

    private static final String COLLECTION_NAME = "travel-sample";
    private static final String QUERY_INDEX_NAME = "testTravelQueryIndex";
    private static final BucketOperator bucketOperator = new BucketOperator(cluster.bucket(TEST_BUCKET));
    private static Collection collection;

    @BeforeAll
    public static void setUpBeforeAll() {
        if (!bucketOperator.hasCollectionInScope(COLLECTION_NAME, TEST_SCOPE)) {
            bucketOperator.createCollection(COLLECTION_NAME, TEST_SCOPE);
        }
        collection = bucketOperator.getCollection(COLLECTION_NAME, TEST_SCOPE);
    }

    @AfterAll
    public static void cleanAfterAll() {
        if (bucketOperator.hasCollectionInScope(COLLECTION_NAME, TEST_SCOPE)) {
            bucketOperator.dropCollection(COLLECTION_NAME, TEST_SCOPE);
        }
    }

    @Test
    @SneakyThrows
    void Query_index_should_be_created_and_rolled_back() {
        Liquibase liquibase = liquibase(CREATE_QUERY_INDEX_TEST_XML);

        liquibase.update();
        assertThat(collection).hasIndex(QUERY_INDEX_NAME);

        liquibase.rollback(1, null);
        assertThat(collection).hasNoIndex(QUERY_INDEX_NAME);
    }
}
