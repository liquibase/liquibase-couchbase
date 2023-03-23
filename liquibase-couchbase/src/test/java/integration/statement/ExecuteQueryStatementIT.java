package integration.statement;

import com.couchbase.client.core.error.ParsingFailureException;
import com.couchbase.client.java.Collection;
import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.ExecuteQueryStatement;
import liquibase.ext.couchbase.types.Param;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.util.Collections;

import static com.couchbase.client.java.json.JsonValue.jo;
import static common.constants.TestConstants.TEST_BUCKET;
import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static liquibase.ext.couchbase.types.Keyspace.keyspace;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ExecuteQueryStatementIT extends ConstantScopeTestCase {
    private static final String DELETE_QUERY = "DELETE FROM testBucket.testScope.testCollection WHERE META().id=$id";
    private final ImmutableList<Param> params = ImmutableList.of(new Param("id", TEST_ID));
    CollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);
    Collection collection = collectionOperator.getCollection();

    @BeforeAll
    static void setUp() {
        bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE).insert(TEST_ID, jo());
        bucketOperator.getCollection(TEST_COLLECTION, TEST_SCOPE).queryIndexes().createPrimaryIndex();
    }

    @AfterAll
    static void tearDown() {
        clusterOperator.dropPrimaryIndex(keyspace(TEST_BUCKET, TEST_SCOPE, TEST_COLLECTION));
    }

    @Test
    void Should_execute_query() {
        ExecuteQueryStatement deleteStmt = new ExecuteQueryStatement(DELETE_QUERY, params);
        deleteStmt.execute(clusterOperator);
        assertThat(collection).doesNotContainId(TEST_ID);
    }

    @Test
    void Should_throw_exception() {
        ExecuteQueryStatement executeQueryStatement =
                new ExecuteQueryStatement("Wrong query", Collections.emptyList());
        assertThatExceptionOfType(ParsingFailureException.class)
                .isThrownBy(() -> executeQueryStatement.execute(clusterOperator));
    }
}
