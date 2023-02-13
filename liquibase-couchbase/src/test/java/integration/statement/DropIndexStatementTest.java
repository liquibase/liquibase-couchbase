package integration.statement;

import com.couchbase.client.java.manager.query.CreateQueryIndexOptions;
import liquibase.ext.couchbase.types.Keyspace;
import common.BucketTestCase;
import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.DropIndexStatement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.couchbase.client.java.manager.query.CreateQueryIndexOptions.createQueryIndexOptions;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.constants.TestConstants.FIELD_1;
import static common.constants.TestConstants.INDEX;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchBaseClusterAssert.assertThat;
import static java.util.Collections.singletonList;


class DropIndexStatementTest extends BucketTestCase {

    private CollectionOperator testCollectionOperator;
    private CollectionOperator defaultCollectionOperator;

    @BeforeEach
    public void setUp() {
        BucketOperator bucketOperator = new BucketOperator(getBucket());
        testCollectionOperator = new CollectionOperator(bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE));
        defaultCollectionOperator = new CollectionOperator(bucketOperator.getCollectionFromDefaultScope(DEFAULT_COLLECTION));
    }

    @Test
    void Should_drop_existing_index_in_default_scope() {
        defaultCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT);
        createIndexInDefaultScope(TEST_BUCKET, INDEX);
        Keyspace keyspace = keyspace(TEST_BUCKET, DEFAULT_SCOPE, DEFAULT_COLLECTION);

        DropIndexStatement statement = new DropIndexStatement(INDEX, keyspace);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHave(INDEX);
        defaultCollectionOperator.removeDoc(TEST_ID);
    }

    @Test
    void Should_drop_index_for_specific_keyspace() {
        testCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT);

        createIndex(TEST_KEYSPACE, INDEX);

        DropIndexStatement statement = new DropIndexStatement(INDEX, TEST_KEYSPACE);
        statement.execute(database.getConnection());

        assertThat(cluster).queryIndexes(TEST_BUCKET).doesNotHave(INDEX);
        testCollectionOperator.removeDoc(TEST_ID);
    }

    private void createIndexInDefaultScope(String bucket, String indexName) {
        cluster.queryIndexes().createIndex(bucket, indexName, singletonList(FIELD_1));
    }

    private void createIndex(Keyspace keyspace, String indexName) {
        CreateQueryIndexOptions options = createQueryIndexOptions()
                .collectionName(keyspace.getCollection())
                .scopeName(keyspace.getScope());
        cluster.queryIndexes()
                .createIndex(keyspace.getBucket(), indexName, singletonList(FIELD_1), options);
    }

}