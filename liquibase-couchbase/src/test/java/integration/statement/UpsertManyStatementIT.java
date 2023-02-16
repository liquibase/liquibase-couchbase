package integration.statement;

import com.couchbase.client.java.Collection;
import com.google.common.collect.Lists;

import liquibase.ext.couchbase.operator.BucketOperator;
import liquibase.ext.couchbase.operator.CollectionOperator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import common.BucketTestCase;
import liquibase.ext.couchbase.statement.UpsertManyStatement;
import liquibase.ext.couchbase.types.Document;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_DOCUMENT_3;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Document.document;

class UpsertManyStatementIT extends BucketTestCase {
    private BucketOperator bucketOperator;
    private CollectionOperator testCollectionOperator;

    @BeforeEach
    public void setUp() {
        bucketOperator = new BucketOperator(getBucket());
        testCollectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    }

    private final List<Document> testDocuments = Lists.newArrayList(
            document(TEST_ID, TEST_DOCUMENT.toString()),
            document(TEST_ID_2, TEST_DOCUMENT_2.toString())
    );

    @Test
    void Should_insert_and_update_many_documents() {
        testCollectionOperator.insertDoc(TEST_ID, TEST_DOCUMENT_3);
        UpsertManyStatement statement = new UpsertManyStatement(TEST_KEYSPACE, testDocuments);

        statement.execute(database.getConnection());

        Collection collection = bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE);
        assertThat(collection).containDocuments(testDocuments);
        testCollectionOperator.removeDocs(TEST_ID, TEST_ID_2);
    }

}
