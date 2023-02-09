package integration.statement;

import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import com.wdt.couchbase.Keyspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.CreateCollectionStatement;
import static com.wdt.couchbase.Keyspace.keyspace;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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
    void Collection_should_be_created_if_it_not_exists() {
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, collectionName);
        CreateCollectionStatement createCollectionStatement =
                new CreateCollectionStatement(keyspace, false);

        createCollectionStatement.execute(database.getConnection());

        assertThat(bucket).hasCollectionInScope(collectionName, DEFAULT_SCOPE);

        dropCollectionInDefaultScope(collectionName);
    }

    @Test
    void Collection_should_not_be_created_again_if_it_exists_and_skip_is_true() {
        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);
        Collection existingCollection = bucket.collection(TEST_COLLECTION);

        CreateCollectionStatement createCollectionStatement = new CreateCollectionStatement(keyspace, true);
        createCollectionStatement.execute(database.getConnection());

        //todo replace with collection assert
        assertThat(bucket.collection(TEST_COLLECTION)).isEqualTo(existingCollection);
        assertThat(bucket).hasCollectionInScope(TEST_COLLECTION, TEST_SCOPE);
    }

    @Test
    void Should_throw_exception_if_collection_exists_and_skip_is_false() {
        Keyspace keyspace = keyspace(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION);

        CreateCollectionStatement createCollectionStatement =
                new CreateCollectionStatement(keyspace, false);

        assertThatExceptionOfType(CollectionExistsException.class)
                .isThrownBy(() -> createCollectionStatement.execute(database.getConnection()));
    }


}
