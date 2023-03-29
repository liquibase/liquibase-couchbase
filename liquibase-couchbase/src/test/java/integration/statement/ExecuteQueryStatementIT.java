package integration.statement;

import com.couchbase.client.core.error.ParsingFailureException;
import common.ConstantScopeTestCase;
import liquibase.ext.couchbase.operator.CollectionOperator;
import liquibase.ext.couchbase.statement.ExecuteQueryStatement;
import liquibase.ext.couchbase.types.Param;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;

import static common.constants.TestConstants.TEST_COLLECTION;
import static common.constants.TestConstants.TEST_DOCUMENT;
import static common.constants.TestConstants.TEST_ID;
import static common.constants.TestConstants.TEST_KEYSPACE;
import static common.constants.TestConstants.TEST_SCOPE;
import static common.matchers.CouchbaseCollectionAssert.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class ExecuteQueryStatementIT extends ConstantScopeTestCase {
    private static final String DELETE_QUERY = String.format("DELETE FROM %s WHERE META().id=$id", TEST_KEYSPACE.getKeyspace());
    private final List<Param> params = ImmutableList.of(new Param(TEST_ID, TEST_ID));
    private static final CollectionOperator collectionOperator = bucketOperator.getCollectionOperator(TEST_COLLECTION, TEST_SCOPE);

    @BeforeAll
    static void setUp() {
        collectionOperator.insertDoc(TEST_DOCUMENT);
        clusterOperator.createCollectionPrimaryIndex(TEST_KEYSPACE, null);
    }

    @AfterAll
    static void tearDown() {
        clusterOperator.dropCollectionPrimaryIndex(TEST_KEYSPACE);
    }

    @Test
    void Should_execute_query() {
        ExecuteQueryStatement deleteStmt = new ExecuteQueryStatement(DELETE_QUERY, params);
        deleteStmt.execute(clusterOperator);
        assertThat(collectionOperator.getCollection()).doesNotContainId(TEST_ID);
    }

    @Test
    void Should_throw_exception() {
        ExecuteQueryStatement executeQueryStatement =
                new ExecuteQueryStatement("Wrong query", Collections.emptyList());
        assertThatExceptionOfType(ParsingFailureException.class)
                .isThrownBy(() -> executeQueryStatement.execute(clusterOperator));
    }
}
