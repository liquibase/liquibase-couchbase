package system.change;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.query.QueryOptions;
import com.couchbase.client.java.query.QueryResult;
import com.couchbase.client.java.query.QueryScanConsistency;
import common.operators.TestCollectionOperator;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import static com.couchbase.client.java.json.JsonValue.jo;
import static com.couchbase.client.java.query.QueryOptions.queryOptions;
import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_SQL_TEST;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENT_ROLLBACK_SQL_TEST;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENT_SQL_TEST;
import static common.constants.TestConstants.CLUSTER_READY_TIMEOUT;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION_SQL;
import static common.constants.TestConstants.TEST_SCOPE_SQL;
import static common.matchers.CouchBaseQueryResultAssert.assertThat;
import static common.matchers.CouchbaseBucketAssert.assertThat;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class SqlFileSystemTest extends LiquibaseSystemTest {

    private static final String TEST_COL_1 = "testCol1";
    private static final String TEST_COL_2 = "testCol2";
    private final JsonObject[] objects = fileSameObjects();

    @Test
    @SneakyThrows
    void Collection_should_be_created_after_liquibase_execution_sql() {
        liquibase(CREATE_COLLECTION_SQL_TEST).update();

        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);

        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope(TEST_COL_1, TEST_SCOPE_SQL);
        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope(TEST_COL_2, TEST_SCOPE_SQL);
    }

    @Test
    @SneakyThrows
    void Documents_should_be_created_after_liquibase_execution_sql() {
        liquibase(INSERT_DOCUMENT_SQL_TEST).update();

        String stmt = format("select * from `%s`.`%s`.`%s`", TEST_BUCKET, TEST_SCOPE_SQL, TEST_COLLECTION_SQL);
        QueryOptions selectQueryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        QueryResult queryResult = cluster.query(stmt, selectQueryOptions);

        assertThat(queryResult).hasSize(2).areContentsEqual(objects, TEST_COLLECTION_SQL);
    }

    @Test
    void Documents_should_not_be_created_after_error_liquibase_execution_sql() {
        Liquibase liquibase = liquibase(INSERT_DOCUMENT_ROLLBACK_SQL_TEST);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        String stmt = format("select * from `%s`.`%s`.`%s`", TEST_BUCKET, TEST_SCOPE_SQL, TEST_COLLECTION_SQL);
        QueryOptions selectQueryOptions = queryOptions().scanConsistency(QueryScanConsistency.REQUEST_PLUS);
        QueryResult queryResult = cluster.query(stmt, selectQueryOptions);

        assertThat(queryResult).isEmpty();
    }

    /**
     * Sometimes we have issue with create index, so added static sleeps along with cluster.waitUntilReady
     */
    @SneakyThrows
    @BeforeAll
    public static void prepareScopeCollection() {
        bucketOperator.createScope(TEST_SCOPE_SQL);
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
        bucketOperator.createCollection(TEST_COLLECTION_SQL, TEST_SCOPE_SQL);
        bucketOperator.getBucket().waitUntilReady(CLUSTER_READY_TIMEOUT);
        TimeUnit.SECONDS.sleep(2L);
        TestCollectionOperator collectionOperator =
                bucketOperator.getCollectionOperator(TEST_COLLECTION_SQL, TEST_SCOPE_SQL);
        collectionOperator.getCollection().queryIndexes().createPrimaryIndex();
        TimeUnit.SECONDS.sleep(2L);
        //TODO investigate how to all avoid static timeouts - issue relates to tests only(waituntilready and watchindexes does not solve issue)
        cluster.waitUntilReady(CLUSTER_READY_TIMEOUT);
    }

    @AfterEach
    public void cleanCollection() {
        String stmt = format("DELETE from `%s`.%s.%s", TEST_BUCKET, TEST_SCOPE_SQL, TEST_COLLECTION_SQL);
        cluster.query(stmt);
    }

    @AfterAll
    public static void cleanTestData() {
        bucketOperator.dropScope(TEST_SCOPE_SQL);
    }

    private JsonObject[] fileSameObjects() {
        return new JsonObject[] {
                jo().put("date", "07/24/2021")
                        .put("flight", "WN533")
                        .put("flighttime", 7713)
                        .put("price", 964.13)
                        .put("route", "63986"),
                jo().put("date", "07/24/2022")
                        .put("flight", "WN534")
                        .put("flighttime", 7717)
                        .put("price", 964.13)
                        .put("route", "63986")
        };
    }
}
