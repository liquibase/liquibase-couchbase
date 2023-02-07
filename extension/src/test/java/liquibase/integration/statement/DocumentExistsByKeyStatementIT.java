package liquibase.integration.statement;

import org.junit.jupiter.api.Test;

import liquibase.ext.statement.DocumentExistsByKeyStatement;
import liquibase.integration.BucketTestCase;
import static liquibase.common.constants.TestConstants.TEST_BUCKET;
import static liquibase.common.constants.TestConstants.TEST_SCOPE;
import static liquibase.common.constants.TestConstants.TEST_COLLECTION;
import static org.assertj.core.api.Assertions.assertThat;

class DocumentExistsByKeyStatementIT extends BucketTestCase {

    @Test
    void Should_return_true_when_document_exists() {
        String key = "key";
        insertRandomValue(key);
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                key
        );

        assertThat(statement.isCDocumentExists(database.getConnection())).isTrue();

        removeFromTestCollection(key);
    }

    @Test
    void Should_return_false_when_document_doesnt_exists() {
        DocumentExistsByKeyStatement statement = new DocumentExistsByKeyStatement(
                TEST_BUCKET,
                TEST_SCOPE,
                TEST_COLLECTION,
                "notExistedKey"
        );

        assertThat(statement.isCDocumentExists(database.getConnection())).isFalse();
    }

    private static void removeFromTestCollection(String key) {
        cluster.bucket(TEST_BUCKET)
                .scope(TEST_SCOPE)
                .collection(TEST_COLLECTION)
                .remove(key);
    }

    private static void insertRandomValue(String key) {
        cluster.bucket(TEST_BUCKET)
                .scope(TEST_SCOPE)
                .collection(TEST_COLLECTION)
                .insert(key, "content");
    }
}
