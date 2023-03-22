package system.change;

import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.json.JsonValue;
import com.couchbase.client.java.query.QueryResult;
import liquibase.Liquibase;
import liquibase.exception.LiquibaseException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import system.LiquibaseSystemTest;

import java.time.Duration;

import static common.constants.ChangeLogSampleFilePaths.CREATE_COLLECTION_SQL_TEST;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENT_ROLLBACK_SQL_TEST;
import static common.constants.ChangeLogSampleFilePaths.INSERT_DOCUMENT_SQL_TEST;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION_N1QL;
import static common.constants.TestConstants.TEST_SCOPE_N1QL;
import static common.matchers.CouchbaseBucketAssert.assertThat;
import static common.matchers.CouchBaseQueryResultAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class N1qlSystemTest extends LiquibaseSystemTest {

    JsonObject[] objects = new JsonObject[] {
            JsonValue.jo().put("date", "07/24/2021")
                    .put("flight", "WN533")
                    .put("flighttime", 7713)
                    .put("price", 964.13)
                    .put("route", "63986"),
            JsonValue.jo().put("date", "07/24/2022")
                    .put("flight", "WN534")
                    .put("flighttime", 7717)
                    .put("price", 964.13)
                    .put("route", "63986")
    };

    @Test
    @SneakyThrows
    void Collection_should_be_created_after_liquibase_execution_n1ql() {
        Liquibase liquibase = liquibase(CREATE_COLLECTION_SQL_TEST);

        liquibase.update();

        cluster.waitUntilReady(Duration.ofMillis(10000));

        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope("testCol1", TEST_SCOPE_N1QL);
        assertThat(cluster.bucket(TEST_BUCKET)).hasCollectionInScope("testCol2", TEST_SCOPE_N1QL);
    }

    @Test
    @SneakyThrows
    void Documents_should_be_created_after_liquibase_execution_n1ql() {
        Liquibase liquibase = liquibase(INSERT_DOCUMENT_SQL_TEST);

        liquibase.update();

        cluster.waitUntilReady(Duration.ofMillis(10000));

        QueryResult all = cluster.query("select * from `testBucket`.`n1qlScope`.`n1qlCollection`");
        assertThat(all).hasSize(2).areContentsEqual(objects, TEST_COLLECTION_N1QL);
    }

    @Test
    @SneakyThrows
    void Documents_should_not_be_created_after_error_liquibase_execution_n1ql() {
        Liquibase liquibase = liquibase(INSERT_DOCUMENT_ROLLBACK_SQL_TEST);

        assertThatExceptionOfType(LiquibaseException.class)
                .isThrownBy(liquibase::update);

        cluster.waitUntilReady(Duration.ofMillis(10000));

        QueryResult all = cluster.query("select * from `testBucket`.`n1qlScope`.`n1qlCollection`");
        assertThat(all).hasSize(0);
    }

    @BeforeAll
    public static void prepareScopeCollection() {
        try {
            // Sometimes we have issue with create index, so added static sleeps instead of cluster.waitUntilReady
            bucketOperator.createScope(TEST_SCOPE_N1QL);
            Thread.sleep(2000);
            bucketOperator.createCollection(TEST_COLLECTION_N1QL, TEST_SCOPE_N1QL);
            Thread.sleep(2000);
            cluster.query("CREATE PRIMARY INDEX idx_n1ql_col_primary on `testBucket`.n1qlScope.n1qlCollection using GSI");
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to init test data");
        }
    }

    @AfterEach
    public void cleanCollection() {
        cluster.query("DELETE from `testBucket`.n1qlScope.n1qlCollection");
    }

    @AfterAll
    public static void cleanTestData() {
        bucketOperator.dropScope(TEST_SCOPE_N1QL);
    }
}
