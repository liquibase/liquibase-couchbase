package integration.statement;

import com.couchbase.client.core.error.CollectionExistsException;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Collection;
import common.RandomizedScopeTestCase;
import liquibase.ext.couchbase.statement.CreateCollectionStatement;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.matchers.CouchBaseBucketAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * Integration test for create collection statement
 */
class CreateCollectionStatementIntegrationTest extends RandomizedScopeTestCase {

    private Keyspace keyspace;

    @BeforeEach
    void setUp() {
        keyspace = keyspace(bucketName, scopeName, collectionName);
    }

    @Test
    void Collection_should_be_created_if_it_not_exists() {
        Keyspace keyspace = keyspace(bucketName, DEFAULT_SCOPE, collectionName);
        CreateCollectionStatement statement =
                new CreateCollectionStatement(keyspace, false);

        statement.execute(database.getConnection());

        assertThat(bucketOperator.getBucket()).hasCollectionInScope(collectionName, DEFAULT_SCOPE);

        bucketOperator.dropCollectionInDefaultScope(collectionName);
    }

    @Test
    void Collection_should_not_be_created_again_if_it_exists_and_skip_is_true() {
        Collection existingCollection = bucketOperator.getCollection(collectionName, scopeName);

        CreateCollectionStatement statement = new CreateCollectionStatement(keyspace, true);
        statement.execute(database.getConnection());

        // todo replace with collection assert
        Bucket bucket = bucketOperator.getBucket();
        assertThat(bucket).hasCollectionInScope(collectionName, scopeName);
        assertThat(bucket.scope(scopeName).collection(collectionName)).isEqualTo(existingCollection);
    }

    @Test
    void Should_throw_exception_if_collection_exists_and_skip_is_false() {
        CreateCollectionStatement statement =
                new CreateCollectionStatement(keyspace, false);

        assertThatExceptionOfType(CollectionExistsException.class)
                .isThrownBy(() -> statement.execute(database.getConnection()));
    }
}
