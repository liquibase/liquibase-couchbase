package liquibase.integration.change;

import static liquibase.common.matchers.CouchbaseBucketAssert.assertThat;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import liquibase.common.TestChangeLogProvider;
import liquibase.ext.changelog.ChangeLogProvider;
import liquibase.ext.database.CouchbaseLiquibaseDatabase;
import liquibase.ext.statement.CreateCollectionStatement;
import liquibase.integration.CouchbaseContainerizedTest;

/**
 * E2E test checking flow from reading xml to applying migration
 */
public class CreateCollectionIntegrationTest extends CouchbaseContainerizedTest {

    private static final String collectionName = "travels";

    protected CouchbaseLiquibaseDatabase database;
    protected ChangeLogProvider changeLogProvider;
    private Cluster cluster;
    private Bucket bucket;

    @BeforeEach
    void setUp() {
        database = new CouchbaseLiquibaseDatabase();
        changeLogProvider = new TestChangeLogProvider(database);
        cluster = Cluster.connect(
                container.getConnectionString(),
                container.getUsername(),
                container.getPassword()
        );
        bucket = cluster.bucket(TEST_BUCKET);
    }

    @Test
    @DisplayName("Should create collection after execute create collection stmt")
    void createCollectionStatementExecute() {
        CreateCollectionStatement createCollectionStatement = new CreateCollectionStatement(collectionName);

        createCollectionStatement.execute(database);

        assertThat(bucket).hasCollectionInDefaultScope(collectionName);
    }


}
