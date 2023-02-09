package integration.statement;

import com.couchbase.client.java.Collection;
import com.google.common.collect.ImmutableMap;
import common.BucketTestCase;
import liquibase.ext.couchbase.statement.UpsertManyStatement;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_DOCUMENT_2;
import static common.constants.TestConstants.TEST_DOCUMENT_3;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_ID_2;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;

class UpsertManyStatementIT extends BucketTestCase {

    private final Map<String, String> testDocuments = ImmutableMap.of(
            TEST_ID, TEST_DOCUMENT,
            TEST_ID_2, TEST_DOCUMENT_2
    );

    @Test
    void Should_insert_and_update_many_documents() {
        insertTestDocument(TEST_DOCUMENT_3);
        UpsertManyStatement statement =
                new UpsertManyStatement(TEST_BUCKET, testDocuments, TEST_SCOPE, TEST_COLLECTION);

        statement.execute(database.getConnection());

        Collection collection = getCollection();
        assertThat(collection).hasDocuments(testDocuments);
    }

}
