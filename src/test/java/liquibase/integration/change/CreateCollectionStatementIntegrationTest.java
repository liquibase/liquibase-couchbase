package liquibase.integration.change;

import com.couchbase.client.java.Bucket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import liquibase.ext.statement.CreateCollectionStatement;
import liquibase.integration.BucketTestCase;
import static liquibase.common.matchers.CouchbaseBucketAssert.assertThat;

/**
 * Integration test for create collection statement
 */
public class CreateCollectionStatementIntegrationTest extends BucketTestCase {

    private static final String collectionName = "travels";

    private Bucket bucket;

    @BeforeEach
    void setUp() {
        bucket = cluster.bucket(TEST_BUCKET);
    }

    @Test
    void Collection_should_appears_in_default_scope_after_execute_create_collection_stmt() {
        CreateCollectionStatement createCollectionStatement = new CreateCollectionStatement(
                TEST_BUCKET, "_default", collectionName);

        createCollectionStatement.execute(database.getConnection());

        assertThat(bucket).hasCollectionInDefaultScope(collectionName);
    }


}
