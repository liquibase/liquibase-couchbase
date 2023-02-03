package liquibase.integration.change;

import static liquibase.common.connection.TestClusterInitializer.connect;
import static liquibase.common.matchers.CouchbaseBucketAssert.assertThat;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.common.connection.TestCouchbaseDatabase;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.statement.CreateCollectionStatement;
import liquibase.integration.CouchbaseContainerizedTest;

/**
 * Integration test for create collection statement
 */
public class CreateCollectionStatementIntegrationTest extends CouchbaseContainerizedTest {

    private static final String collectionName = "travels";

    private Cluster cluster;
    private Bucket bucket;
    private CouchbaseLiquibaseDatabase database;

    @BeforeEach
    void setUp() {
        database = new TestCouchbaseDatabase(container);
        cluster = connect(container);
        bucket = cluster.bucket(TEST_BUCKET);
    }

    @Test
    void Collection_should_appears_in_default_scope_after_execute_create_collection_stmt() {
        CreateCollectionStatement createCollectionStatement = new CreateCollectionStatement(collectionName);

        createCollectionStatement.execute(database.getConnection());

        assertThat(bucket).hasCollectionInDefaultScope(collectionName);
    }


}
