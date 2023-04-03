package integration.statement;

import common.RandomizedScopeTestCase;
import common.operators.TestCollectionOperator;
import liquibase.ext.couchbase.statement.DropIndexStatement;
import liquibase.ext.couchbase.types.Document;
import liquibase.ext.couchbase.types.Field;
import liquibase.ext.couchbase.types.Keyspace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static common.constants.TestConstants.DEFAULT_COLLECTION;
import static common.constants.TestConstants.DEFAULT_SCOPE;
import static common.matchers.CouchbaseClusterAssert.assertThat;
import static java.util.Collections.singletonList;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;


class DropIndexStatementIT extends RandomizedScopeTestCase {

    private Keyspace keyspace;
    private Document doc;

    @BeforeEach
    public void setUp() {
        TestCollectionOperator collectionOperator = bucketOperator.getCollectionOperator(collectionName, scopeName);
        keyspace = keyspace(bucketName, scopeName, collectionName);
        doc = collectionOperator.generateTestDoc();
        collectionOperator.insertDoc(doc);
    }

    @Test
    void Should_drop_existing_index_in_default_scope() {
        String randomIndexName = clusterOperator.getTestIndexId();
        clusterOperator.createCollectionQueryIndex(randomIndexName, keyspace, singletonList(getFirstField(doc)));

        DropIndexStatement statement = new DropIndexStatement(false, randomIndexName, keyspace);
        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).doesNotHave(randomIndexName);
    }

    @Test
    void Should_drop_index_for_specific_keyspace() {
        String randomIndexName = clusterOperator.getTestIndexId();
        Keyspace keyspace = keyspace(bucketName, DEFAULT_SCOPE, DEFAULT_COLLECTION);
        clusterOperator.createCollectionQueryIndex(randomIndexName, keyspace, singletonList(getFirstField(doc)));

        DropIndexStatement statement = new DropIndexStatement(false, randomIndexName, keyspace);
        statement.execute(clusterOperator);

        assertThat(cluster).queryIndexes(bucketName).doesNotHave(randomIndexName);
    }

    private static Field getFirstField(Document doc) {
        return doc.getContentAsJson().getNames().stream().findFirst().map(Field::new).get();
    }

}