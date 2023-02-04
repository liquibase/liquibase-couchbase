package liquibase.integration.statement;

import org.junit.jupiter.api.Test;

import liquibase.ext.statement.CollectionExistsStatement;
import liquibase.integration.BucketTestCase;
import static org.assertj.core.api.Assertions.assertThat;

class CollectionExistsStatementIT extends BucketTestCase {

    @Test
    void Should_return_true_when_collection_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, TEST_SCOPE,
                TEST_COLLECTION);

        assertThat(statement.isCollectionExists(database.getConnection())).isTrue();
    }

    @Test
    void Should_return_false_when_collection_doesnt_exists() {
        CollectionExistsStatement statement = new CollectionExistsStatement(TEST_BUCKET, TEST_SCOPE,
                "notCreatedCollection");

        assertThat(statement.isCollectionExists(database.getConnection())).isFalse();
    }
}
